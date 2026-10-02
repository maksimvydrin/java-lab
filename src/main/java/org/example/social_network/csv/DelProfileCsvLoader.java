package org.example.social_network.csv;

import org.example.social_network.model.DelProfile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DelProfileCsvLoader {

    public List<DelProfile> load(Path file) {

        List<DelProfile> result = new ArrayList<>();

        try (var reader = Files.newBufferedReader(
                file,
                Charset.forName("Windows-1251")
        )) {

            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {

                try {

                    String[] parts = line.split(";", -1);

                    if (parts.length != 5) {
                        continue;
                    }

                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String city = parts[2].trim();
                    int birthYear = Integer.parseInt(parts[3].trim());
                    String delReason = parts[4].trim();

                    DelProfile delProfile = new DelProfile(
                            id,
                            name,
                            city,
                            birthYear,
                            delReason
                    );

                    result.add(delProfile);

                } catch (NumberFormatException e) {
                    System.out.println("Ошибочная строка пропущена");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Ошибка чтения файла: " + file,
                    e
            );
        }

        return result;
    }
}