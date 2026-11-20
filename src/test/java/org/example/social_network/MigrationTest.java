package org.example.social_network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.social_network.model.Profile;
import org.example.social_network.serialization.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MigrationTest {

    @TempDir
    Path tempDir;

    //поэтапная миграция JSON v1 в v2 в v3
    @Test
    void testJsonMigratesV1toV2toV3() throws Exception {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        Path file = tempDir.resolve("v1.json");
        String v1Json = """
            {
              "schema_version": 1,
              "profiles": [ { "id": 1, "name": "Анна", "city": "Новосибирск", "birthYear": 2000 } ]
            }
            """;
        Files.writeString(file, v1Json);

        SocialNetworkData loaded = serializer.load(file, (oldV, newV) -> true);

        assertEquals(3, loaded.getVersion());
        assertEquals(0.0, loaded.getProfiles()[0].getRating());
        assertEquals("", loaded.getProfiles()[0].getNote());
    }

    //миграция JSON v2 в v3 (сохраняет существующий rating, добавляет базовый note)
    @Test
    void testJsonMigratesV2toV3() throws Exception {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        Path file = tempDir.resolve("v2.json");
        String v2Json = """
            {
              "schema_version": 2,
              "profiles": [ { "id": 1, "name": "Анна", "city": "Новосибирск", "birthYear": 2000, "rating": 8.5 } ]
            }
            """;
        Files.writeString(file, v2Json);

        SocialNetworkData loaded = serializer.load(file, (oldV, newV) -> true);

        assertEquals(3, loaded.getVersion());
        assertEquals(8.5, loaded.getProfiles()[0].getRating());
        assertEquals("", loaded.getProfiles()[0].getNote());
    }

    //даунгрейд JSON v3 в v2 и v3 в v1
    @Test
    void testJsonDowngradesV3toV2andV1() throws Exception {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        ObjectMapper mapper = new ObjectMapper();

        SocialNetworkData data = new SocialNetworkData();
        data.setProfiles(new Profile[]{ new Profile(1, "Анна", "Москва", 2000, 9.5, "Важно") });

        Path fileV2 = tempDir.resolve("down_v2.json");
        serializer.save(data, fileV2, 2);
        String rawV2 = Files.readString(fileV2);
        assertTrue(rawV2.contains("rating"));
        assertFalse(rawV2.contains("note"));
        assertEquals(2, ((ObjectNode) mapper.readTree(fileV2.toFile())).get("schema_version").asInt());

        Path fileV1 = tempDir.resolve("down_v1.json");
        serializer.save(data, fileV1, 1);
        String rawV1 = Files.readString(fileV1);
        assertFalse(rawV1.contains("rating"));
        assertFalse(rawV1.contains("note"));
        assertEquals(1, ((ObjectNode) mapper.readTree(fileV1.toFile())).get("schema_version").asInt());
    }

    //бинарный формат: даунгрейд в v1 и поэтапная миграция v1 в v2 в v3
    @Test
    void testBinaryDowngradeAndMigrateV1toV3() throws Exception {
        Serializer<SocialNetworkData> serializer = new BinSocialSerializer();
        Path file = tempDir.resolve("v1.bin");

        Profile originalProfile = new Profile(1, "Борис", "Казань", 1995, 7.5, "Заметка");
        SocialNetworkData data = new SocialNetworkData();
        data.setProfiles(new Profile[]{ originalProfile });

        serializer.save(data, file, 1);
        // Исходный объект в памяти не должен испортиться
        assertEquals(7.5, originalProfile.getRating());
        assertEquals("Заметка", originalProfile.getNote());

        SocialNetworkData loaded = serializer.load(file, (oldV, newV) -> true);
        assertEquals(3, loaded.getVersion());
        assertEquals(0.0, loaded.getProfiles()[0].getRating());
        assertEquals("", loaded.getProfiles()[0].getNote());
    }

    //негативный:отказ пользователя от миграции (JSON и BIN)
    @Test
    void testMigrationRejected_throwsException() throws Exception {
        Serializer<SocialNetworkData> jsonSer = new JsonSocialSerializer();
        Serializer<SocialNetworkData> binSer = new BinSocialSerializer();

        Path jsonFile = tempDir.resolve("reject.json");
        Files.writeString(jsonFile, "{ \"schema_version\": 1, \"profiles\": [] }");
        IOException exJson = assertThrows(IOException.class, () -> jsonSer.load(jsonFile, (o, n) -> false));
        assertTrue(exJson.getMessage().contains("отменена"));

        Path binFile = tempDir.resolve("reject.bin");
        binSer.save(new SocialNetworkData(), binFile, 1);
        IOException exBin = assertThrows(IOException.class, () -> binSer.load(binFile, (o, n) -> false));
        assertTrue(exBin.getMessage().contains("отменена"));
    }

    //негативный: файл из будущего
    @Test
    void testFutureVersion_throwsException() throws Exception {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        Path file = tempDir.resolve("future.json");
        Files.writeString(file, "{ \"schema_version\": 99, \"profiles\": [] }");

        IOException ex = assertThrows(IOException.class, () -> serializer.load(file, (o, n) -> true));
        assertTrue(ex.getMessage().contains("будущего"));
    }

    //негативный: неверно заполненное поле, обнаруженное при миграции (rating вне диапазона)
    @Test
    void testInvalidFieldDuringMigration_throwsException() throws Exception {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        Path file = tempDir.resolve("bad_rating.json");
        String badV2Json = """
            {
              "schema_version": 2,
              "profiles": [ { "id": 1, "name": "Анна", "city": "Москва", "birthYear": 2000, "rating": -5.0 } ]
            }
            """;
        Files.writeString(file, badV2Json);

        IOException ex = assertThrows(IOException.class, () -> serializer.load(file, (o, n) -> true));
        assertTrue(ex.getMessage().contains("rating"));
    }

    //гегативный: битый JSON-файл
    @Test
    void testCorruptedJson_throwsException() throws Exception {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        Path file = tempDir.resolve("broken.json");
        Files.writeString(file, "{ \"schema_version\": 1, \"profiles\": [ { broken ");

        assertThrows(IOException.class, () -> serializer.load(file, (o, n) -> true));
    }
}
