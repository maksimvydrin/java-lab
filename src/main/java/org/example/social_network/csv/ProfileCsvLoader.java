package org.example.social_network.csv;

import org.example.social_network.exception.*;
import org.example.social_network.model.Profile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProfileCsvLoader {

    public LoadCsvResult<Profile> load(Path file) throws LoadCsvException {

        List<Profile> result = new ArrayList<>();
        List<LoadCsvException> errors = new ArrayList<>();

        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(";", -1);

                    if (parts.length != 4) {
                        errors.add(new LoadCsvException(ErrorCsv.WRONG_FIELD_COUNT, lineNumber, "Ожидалось 4 поля"));
                        continue;
                    }

                    int id;

                    try {
                        id = Integer.parseInt(parts[0]);
                    } catch (NumberFormatException e) {
                        errors.add(new LoadCsvException(ErrorCsv.BAD_NUMBER, lineNumber, "ID должен быть числом"));
                        continue;
                    }

                    String name = parts[1];
                    String city = parts[2];

                    int birthYear;

                    try {
                        birthYear = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        errors.add(new LoadCsvException(ErrorCsv.BAD_NUMBER, lineNumber, "Год рождения должен быть числом"));
                        continue;
                    }

                    Profile profile = new Profile(id, name, city, birthYear);

                    List<String> validationErrors = profile.validate();

                    if (!validationErrors.isEmpty()) {
                        errors.add(new LoadCsvException(ErrorCsv.INVALID_DATA, lineNumber, String.join("; ", validationErrors)));
                        continue;
                    }

                    result.add(profile);

                } catch (Exception e) {
                    errors.add(new LoadCsvException(ErrorCsv.INVALID_DATA, lineNumber, e.getMessage()));
                }
            }

        } catch (IOException e) {
            throw new LoadCsvException(ErrorCsv.IO_ERROR, 0, "Ошибка чтения файла: " + file);
        }

        return new LoadCsvResult<>(result, errors);
    }
}