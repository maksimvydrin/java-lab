package org.example.social_network.csv;
import org.example.social_network.model.Community;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CommunityCsvSaver {

    public void save(List<Community> communities, Path file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            writer.write("id;name;city;birthYear,adminId");
            writer.newLine();

            for (Community community : communities) {
                writer.write(Integer.toString(community.getId()));
                writer.write(";");
                writer.write(clean(community.getName()));
                writer.write(";");
                writer.write(clean(community.getCity()));
                writer.write(";");
                writer.write(Integer.toString(community.getBirthYear()));
                writer.newLine();
                writer.write(Integer.toString(community.getAdministratorId()));
                writer.write(";");
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