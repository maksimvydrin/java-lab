package org.example.social_network.csv;
import org.example.social_network.model.Profile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProfileCsvLoader {
    public List<Profile> load(Path file){
        List<Profile> result = new ArrayList<>();
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(";", -1);

                    if (parts.length != 4) {
                        continue;
                    }
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String city = parts[2];
                    int birthYear = Integer.parseInt(parts[3]);

                    Profile profile = new Profile(id, name, city, birthYear);

                    if (!profile.validate().isEmpty()) {
                        continue;
                    }
                    result.add(profile);
                } catch (Exception e) {
                    System.out.println("Ошибочная строка пропущена");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + file, e);
        }
        return result;
    }
}

