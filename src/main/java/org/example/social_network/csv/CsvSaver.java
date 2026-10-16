
package org.example.social_network.csv;

import org.example.social_network.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvSaver {

    public <T> void save(List<T> items, Path file) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Для пустого списка необходимо указать заголовок CSV"
            );
        }

        save(items, file, getHeader(items.get(0)));
    }

    public <T> void save(
            List<T> items,
            Path file,
            String header
    ) {
        if (items == null) {
            throw new IllegalArgumentException(
                    "Список не должен быть null"
            );
        }

        if (!isSupportedHeader(header)) {
            throw new IllegalArgumentException(
                    "Неизвестный заголовок CSV: " + header
            );
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8)) {

            writer.write(header);
            writer.newLine();

            for (T item : items) {
                if (item == null) {
                    throw new IllegalArgumentException(
                            "Список содержит null"
                    );
                }

                writer.write(formatRow(item));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Ошибка записи CSV-файла: " + file,
                    e
            );
        }
    }

    private boolean isSupportedHeader(String header) {
        return "id;name;city;birthYear".equals(header)
                || "id;name;city;birthYear;adminId".equals(header)
                || "id;name;city;birthYear;dayDel;delReason".equals(header)
                || "profile1;profile2;strength".equals(header);
    }

    private String getHeader(Object example) {
        if (example instanceof DelProfile) {
            return "id;name;city;birthYear;dayDel;delReason";
        }

        if (example instanceof Community) {
            return "id;name;city;birthYear;adminId";
        }

        if (example instanceof Profile) {
            return "id;name;city;birthYear";
        }

        if (example instanceof FriendShip) {
            return "profile1;profile2;strength";
        }

        throw new IllegalArgumentException(
                "Неизвестный тип объекта для CSV"
        );
    }

    private String formatRow(Object item) {
        if (item instanceof DelProfile p) {
            return p.getId() + ";"
                    + clean(p.getName()) + ";"
                    + clean(p.getCity()) + ";"
                    + p.getBirthYear() + ";"
                    + p.getDayDel() + ";"
                    + clean(p.getDelReason());
        }

        if (item instanceof Community c) {
            return c.getId() + ";"
                    + clean(c.getName()) + ";"
                    + clean(c.getCity()) + ";"
                    + c.getBirthYear() + ";"
                    + c.getAdminId();
        }

        if (item instanceof FriendShip f) {
            return f.getProfile1() + ";"
                    + f.getProfile2() + ";"
                    + f.getStrength();
        }

        if (item instanceof Profile p) {
            return p.getId() + ";"
                    + clean(p.getName()) + ";"
                    + clean(p.getCity()) + ";"
                    + p.getBirthYear();
        }

        throw new IllegalArgumentException(
                "Неизвестный тип объекта для CSV"
        );
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value.replace(";", ",")
                .replace("\r", " ")
                .replace("\n", " ");
    }
}
