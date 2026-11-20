package org.example.social_network.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

public final class JsonSocialSerializer implements Serializer<SocialNetworkData> {
    private final ObjectMapper mapper;

    public JsonSocialSerializer() {
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public String format() {
        return "json";
    }

    @Override
    public void save(SocialNetworkData data, Path file, int targetVersion) throws IOException {
        if (data == null) throw new IllegalArgumentException("Данные для сохранения не могут быть null");
        if (targetVersion < 1 || targetVersion > SocialNetworkData.CURRENT_VERSION) {
            throw new IOException("Неподдерживаемая целевая версия для сохранения: " + targetVersion);
        }

        ObjectNode root = mapper.valueToTree(data);
        int current = SocialNetworkData.CURRENT_VERSION;

        while (current > targetVersion) {
            if (current == 3) {
                root = MigrationManager.downgradeJsonV3toV2(root);
            } else if (current == 2) {
                root = MigrationManager.downgradeJsonV2toV1(root);
            }
            current--;
        }

        mapper.writeValue(file.toFile(), root);
    }

    @Override
    public SocialNetworkData load(Path file, BiFunction<Integer, Integer, Boolean> migrationApprover) throws IOException {
        if (file == null || !Files.exists(file)) throw new IOException("Файл не существует: " + file);
        if (Files.size(file) == 0) throw new IOException("Файл пуст: " + file);

        var tree = mapper.readTree(file.toFile());
        if (!(tree instanceof ObjectNode root)) {
            throw new IOException("Некорректная структура JSON-файла");
        }

        int fileVersion;
        if (root.has("schema_version")) {
            fileVersion = root.get("schema_version").asInt();
        } else if (root.has("version")) {
            fileVersion = root.get("version").asInt();
        } else {
            fileVersion = 1;
        }

        if (fileVersion < 1) {
            throw new IOException("Некорректная версия файла: " + fileVersion);
        }
        if (fileVersion > SocialNetworkData.CURRENT_VERSION) {
            throw new IOException("Файл из будущего: версия " + fileVersion
                    + " (поддерживается до " + SocialNetworkData.CURRENT_VERSION + ")");
        }

        if (fileVersion < SocialNetworkData.CURRENT_VERSION) {
            if (migrationApprover != null && !migrationApprover.apply(fileVersion, SocialNetworkData.CURRENT_VERSION)) {
                throw new IOException("Миграция отменена пользователем");
            }
            while (fileVersion < SocialNetworkData.CURRENT_VERSION) {
                if (fileVersion == 1) {
                    root = MigrationManager.migrateJsonV1toV2(root);
                    fileVersion = 2;
                } else if (fileVersion == 2) {
                    root = MigrationManager.migrateJsonV2toV3(root);
                    fileVersion = 3;
                }
            }
        }

        SocialNetworkData data = mapper.treeToValue(root, SocialNetworkData.class);
        MigrationManager.validateMigratedData(data);
        return data;
    }
}