package org.example.social_network.csv;
import org.example.social_network.model.Community;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommunityCsvLoader {

    public List<Community> load(Path file) {
        List<Community> result = new ArrayList<>();

        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(";", -1);
                    if (parts.length != 5)
                    {
                        continue;
                    }
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String city = parts[2];
                    int birthYear = Integer.parseInt(parts[3]);
                    int adminId = Integer.parseInt(parts[4]);

                    Community community = new Community(id, name, city, birthYear, adminId);

                    if (!community.validate().isEmpty())
                    {
                        continue;
                    }

                    result.add(community);

                }
                catch (NumberFormatException e)
                {
                    System.out.println("Ошибочная строка пропущена");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + file, e);
        }

        return result;
    }
}