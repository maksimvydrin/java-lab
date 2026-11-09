package org.example.social_network;

import org.example.social_network.csv.CsvLoader;
import org.example.social_network.exception.LoadCsvResult;
import org.example.social_network.graph.GraphAlgorithms;
import org.example.social_network.graph.SocialGraph;
import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;
import org.example.social_network.serialization.BinSocialSerializer;
import org.example.social_network.serialization.JsonSocialSerializer;
import org.example.social_network.serialization.Serializer;
import org.example.social_network.serialization.SocialNetworkData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SerializationAndBasicOpsTest {

    @TempDir
    Path tempDir;

    private SocialNetworkData createSampleData() {
        Profile[] profiles = new Profile[] {
                new Profile(1, "Анна", "Новосибирск", 1999),
                new Profile(2, "Борис", "Москва", 1985)
        };
        Community[] communities = new Community[] {
                new Community(10, "НГУ ИТ", "Новосибирск", 2015, 1)
        };
        DelProfile[] deleted = new DelProfile[] {
                new DelProfile(3, "Удаленный", "Казань", 1990, 12, "Собственное желание")
        };
        FriendShip[] friendships = new FriendShip[] {
                new FriendShip(1, 2, 8)
        };

        return new SocialNetworkData(profiles, communities, deleted, friendships);
    }

    // 1. Позитивный тест: Roundtrip для JSON
    @Test
    void jsonRoundtrip_preservesDataExactly() throws IOException {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        SocialNetworkData original = createSampleData();
        Path file = tempDir.resolve("test_network.json");

        serializer.save(original, file);
        SocialNetworkData loaded = serializer.load(file);

        assertEquals(original, loaded);
    }

    // 2. Позитивный тест: Roundtrip для бинарного формата
    @Test
    void binRoundtrip_preservesDataExactly() throws IOException {
        Serializer<SocialNetworkData> serializer = new BinSocialSerializer();
        SocialNetworkData original = createSampleData();
        Path file = tempDir.resolve("test_network.bin");

        serializer.save(original, file);
        SocialNetworkData loaded = serializer.load(file);

        assertEquals(original, loaded);
    }

    // 3. Позитивный тест: Чтение эталонного JSON-файла из resources
    @Test
    void jsonSerializer_loadsReferenceFileFromResources() throws IOException, URISyntaxException {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        var url = getClass().getResource("/reference_network.json");
        assertNotNull(url, "Эталонный файл reference_network.json не найден в resources");

        SocialNetworkData loaded = serializer.load(Path.of(url.toURI()));
        assertEquals(2, loaded.getProfiles().length);
        assertEquals("Анна", loaded.getProfiles()[0].getName());
        assertEquals(1, loaded.getFriendships().length);
        assertEquals(8, loaded.getFriendships()[0].getStrength());
    }

    // 4. Позитивный тест: Чтение эталонного CSV-файла и проверка базовых операций графа
    @Test
    void csvLoaderAndGraph_loadsReferenceCsvAndFindsShortestPath() throws Exception {
        var url = getClass().getResource("/reference_profiles.csv");
        assertNotNull(url, "Эталонный файл reference_profiles.csv не найден в resources");

        CsvLoader loader = new CsvLoader();
        LoadCsvResult<Profile> result = loader.load(Path.of(url.toURI()));

        assertTrue(result.getErrors().isEmpty());
        assertEquals(2, result.getItems().size());

        SocialGraph<Integer, Profile> graph = new SocialGraph<>();
        for (Profile p : result.getItems()) {
            graph.addVertex(p.getId(), p);
        }
        graph.addEdge(1, 2, 5);

        GraphAlgorithms algo = new GraphAlgorithms();
        assertEquals(List.of(1, 2), algo.dijkstra(graph, 1, 2));
    }

    // 5. Негативный тест: Битый JSON-файл вызывает IOException
    @Test
    void jsonSerializer_throwsIOExceptionOnCorruptedFile() throws IOException {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        Path corruptedFile = tempDir.resolve("corrupted.json");
        Files.writeString(corruptedFile, "{ \"version\": 1, \"profiles\": [ { broken_json ", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> serializer.load(corruptedFile));
    }

    // 6. Негативный тест: Битый бинарный файл вызывает IOException
    @Test
    void binSerializer_throwsIOExceptionOnCorruptedFile() throws IOException {
        Serializer<SocialNetworkData> serializer = new BinSocialSerializer();
        Path corruptedBin = tempDir.resolve("corrupted.bin");
        Files.write(corruptedBin, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05});

        assertThrows(IOException.class, () -> serializer.load(corruptedBin));
    }

    // 7. Негативный тест: Несовпадение версии формата в JSON вызывает IOException
    @Test
    void jsonSerializer_throwsIOExceptionOnUnsupportedVersion() throws IOException {
        Serializer<SocialNetworkData> serializer = new JsonSocialSerializer();
        Path wrongVersionFile = tempDir.resolve("wrong_version.json");
        String jsonWithWrongVersion = """
                {
                  "version": 999,
                  "profiles": [],
                  "communities": [],
                  "deletedProfiles": [],
                  "friendships": []
                }
                """;
        Files.writeString(wrongVersionFile, jsonWithWrongVersion, StandardCharsets.UTF_8);

        IOException ex = assertThrows(IOException.class, () -> serializer.load(wrongVersionFile));
        assertTrue(ex.getMessage().contains("версия"));
    }

    // 8. Негативный тест: Загрузка пустого или несуществующего файла вызывает IOException
    @Test
    void serializers_throwIOExceptionOnEmptyOrMissingFile() throws IOException {
        Serializer<SocialNetworkData> jsonSerializer = new JsonSocialSerializer();
        Serializer<SocialNetworkData> binSerializer = new BinSocialSerializer();

        Path emptyFile = tempDir.resolve("empty.dat");
        Files.createFile(emptyFile);
        Path missingFile = tempDir.resolve("non_existent.dat");

        assertThrows(IOException.class, () -> jsonSerializer.load(emptyFile));
        assertThrows(IOException.class, () -> binSerializer.load(emptyFile));
        assertThrows(IOException.class, () -> jsonSerializer.load(missingFile));
        assertThrows(IOException.class, () -> binSerializer.load(missingFile));
    }
}