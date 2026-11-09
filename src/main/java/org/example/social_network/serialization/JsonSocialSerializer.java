package org.example.social_network.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public void save(SocialNetworkData data, Path file) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("Данные для сохранения не могут быть null");
        }
        mapper.writeValue(file.toFile(), data);
    }

    @Override
    public SocialNetworkData load(Path file) throws IOException {
        if (file == null || !Files.exists(file)) {
            throw new IOException("Файл не существует: " + file);
        }
        if (Files.size(file) == 0) {
            throw new IOException("Файл пуст: " + file);
        }

        SocialNetworkData data = mapper.readValue(file.toFile(), SocialNetworkData.class);

        if (data == null) {
            throw new IOException("Не удалось прочитать данные из JSON");
        }
        if (data.getVersion() != SocialNetworkData.CURRENT_VERSION) {
            throw new IOException("Неподдерживаемая версия формата JSON: " + data.getVersion()
                    + " (ожидалась " + SocialNetworkData.CURRENT_VERSION + ")");
        }

        return data;
    }
}