package org.example.social_network.serialization;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

public final class BinSocialSerializer implements Serializer<SocialNetworkData> {

    @Override
    public String format() {
        return "bin";
    }

    @Override
    public void save(SocialNetworkData data, Path file, int targetVersion) throws IOException {
        if (data == null) throw new IllegalArgumentException("Данные не могут быть null");
        if (targetVersion < 1 || targetVersion > SocialNetworkData.CURRENT_VERSION) {
            throw new IOException("Неподдерживаемая целевая версия для сохранения: " + targetVersion);
        }

        SocialNetworkData toWrite = (targetVersion < SocialNetworkData.CURRENT_VERSION)
                ? MigrationManager.createDowngradedCopy(data, targetVersion)
                : data;

        toWrite.setTargetVersion(targetVersion);
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            oos.writeObject(toWrite);
        } finally {
            toWrite.setTargetVersion(SocialNetworkData.CURRENT_VERSION);
        }
    }

    @Override
    public SocialNetworkData load(Path file, BiFunction<Integer, Integer, Boolean> migrationApprover) throws IOException {
        if (file == null || !Files.exists(file)) throw new IOException("Файл не существует");
        if (Files.size(file) == 0) throw new IOException("Файл пуст");

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            Object obj = ois.readObject();
            if (!(obj instanceof SocialNetworkData data)) {
                throw new IOException("Некорректный тип объекта");
            }

            int fileVersion = data.getVersion();
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
                        MigrationManager.migrateObjectV1toV2(data);
                        fileVersion = 2;
                    } else if (fileVersion == 2) {
                        MigrationManager.migrateObjectV2toV3(data);
                        fileVersion = 3;
                    }
                }
            }

            MigrationManager.validateMigratedData(data);
            return data;
        } catch (ClassNotFoundException e) {
            throw new IOException("Класс данных не найден", e);
        }
    }
}