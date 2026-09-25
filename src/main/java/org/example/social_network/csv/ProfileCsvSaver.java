package org.example.social_network.csv;
import org.example.social_network.model.Profile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProfileCsvSaver {

    public void save(Path file, List<Profile> profiles) {
        try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id;name;city;birthYear");
            writer.newLine();

            for (Profile profile : profiles)
            {
                writer.write(profile.getId() + ";" + profile.getName() + ";" + profile.getCity() + ";" + profile.getBirthYear());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи файла: " + file, e);
        }
    }
}