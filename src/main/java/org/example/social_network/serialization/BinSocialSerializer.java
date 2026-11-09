package org.example.social_network.serialization;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BinSocialSerializer implements Serializer<SocialNetworkData> {

    @Override
    public String format() {
        return "bin";
    }

    @Override
    public void save(SocialNetworkData data, Path file) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("Данные для сохранения не могут быть null");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            oos.writeObject(data);
        }
    }

    @Override
    public SocialNetworkData load(Path file) throws IOException {
        if (file == null || !Files.exists(file)) {
            throw new IOException("Файл не существует: " + file);
        }
        if (Files.size(file) == 0) {
            throw new IOException("Файл пуст: " + file);
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            Object obj = ois.readObject();
            if (!(obj instanceof SocialNetworkData data)) {
                throw new IOException("Некорректный тип объекта внутри бинарного файла");
            }
            if (data.getVersion() != SocialNetworkData.CURRENT_VERSION) {
                throw new IOException("Неподдерживаемая версия данных: " + data.getVersion()
                        + " (ожидалась " + SocialNetworkData.CURRENT_VERSION + ")");
            }
            return data;
        } catch (ClassNotFoundException e) {
            throw new IOException("Класс данных не найден при десериализации", e);
        }
    }
}