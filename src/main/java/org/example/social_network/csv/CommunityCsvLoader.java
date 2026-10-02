package org.example.social_network.csv;

import org.example.social_network.exception.ErrorCsv;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.exception.LoadCsvResult;
import org.example.social_network.model.Community;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommunityCsvLoader {
    public LoadCsvResult<Community> load(Path file) throws LoadCsvException {
        List<Community> result = new ArrayList<>();
        List<LoadCsvException> errors = new ArrayList<>();

        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(";", -1);
                    if (parts.length != 5) {
                        throw new LoadCsvException(
                                ErrorCsv.WRONG_FIELD_COUNT,
                                lineNumber,
                                "Ожидалось 5 полей, получено " + parts.length);
                    }

                    int id = parseInt(parts[0], lineNumber, "ID");
                    String name = parts[1].trim();
                    String city = parts[2].trim();
                    int birthYear = parseInt(parts[3], lineNumber, "год создания");
                    int adminId = parseInt(parts[4], lineNumber, "ID администратора");

                    Community community = new Community(id, name, city, birthYear, adminId);
                    List<String> validationErrors = community.validate();
                    if (!validationErrors.isEmpty()) {
                        throw new LoadCsvException(
                                ErrorCsv.INVALID_DATA,
                                lineNumber,
                                String.join("; ", validationErrors));
                    }

                    result.add(community);
                } catch (LoadCsvException e) {
                    errors.add(e);
                }
            }
        } catch (IOException e) {
            throw new LoadCsvException(ErrorCsv.IO_ERROR, 0, "Ошибка чтения файла: " + file, e);
        }

        return new LoadCsvResult<>(result, errors);
    }

    private int parseInt(String value, int lineNumber, String fieldName) throws LoadCsvException {
        try {return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new LoadCsvException(ErrorCsv.BAD_NUMBER, lineNumber, "Поле '" + fieldName + "' должно быть числом");
        }
    }
}
