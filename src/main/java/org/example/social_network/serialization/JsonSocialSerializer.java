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
        ObjectNode root = mapper.valueToTree(data);

        if (targetVersion == 1) {
            root = MigrationManager.downgradeJsonV2toV1(root);
        }

        mapper.writeValue(file.toFile(), root);
    }

    @Override
    public SocialNetworkData load(Path file, BiFunction<Integer, Integer, Boolean> migrationApprover) throws IOException {
        if (file == null || !Files.exists(file)) throw new IOException("Файл не существует: " + file);
        if (Files.size(file) == 0) throw new IOException("Файл пуст: " + file);

        ObjectNode root = (ObjectNode) mapper.readTree(file.toFile());
        if (root == null) throw new IOException("Не удалось прочитать данные из JSON");

        int fileVersion = root.has("version") ? root.get("version").asInt() : 1;

        if (fileVersion < SocialNetworkData.CURRENT_VERSION) {
            if (migrationApprover != null && !migrationApprover.apply(fileVersion, SocialNetworkData.CURRENT_VERSION)) {
                throw new IOException("Миграция отменена пользователем");
            }
            // Цепочка миграций
            if (fileVersion == 1) {
                root = MigrationManager.migrateJsonV1toV2(root);
                fileVersion = 2;
            }
        } else if (fileVersion > SocialNetworkData.CURRENT_VERSION) {
            throw new IOException("Файл из будущего: версия " + fileVersion);
        }

        return mapper.treeToValue(root, SocialNetworkData.class);
    }
}