package org.example.social_network.csv;

import org.example.social_network.model.Profile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProfileCsvSaver {

    public void save(List<Profile> profiles, Path file) {
        try (BufferedWriter writer =
                     Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            writer.write("id;name;city;birthYear");
            writer.newLine();

            for (Profile profile : profiles) {
                writer.write(Integer.toString(profile.getId()));
                writer.write(";");
                writer.write(clean(profile.getName()));
                writer.write(";");
                writer.write(clean(profile.getCity()));
                writer.write(";");
                writer.write(Integer.toString(profile.getBirthYear()));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи файла: " + file, e);
        }
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace(";", ",")
                .replace("\r", " ")
                .replace("\n", " ");
    }
}