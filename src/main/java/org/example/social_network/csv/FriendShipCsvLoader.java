package org.example.social_network.csv;

import org.example.social_network.exception.ErrorCsv;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.exception.LoadCsvResult;
import org.example.social_network.model.FriendShip;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FriendShipCsvLoader {
    public LoadCsvResult<FriendShip> load(Path file) throws LoadCsvException {
        List<FriendShip> result = new ArrayList<>();
        List<LoadCsvException> errors = new ArrayList<>();

        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(";", -1);
                    if (parts.length != 3) {
                        throw new LoadCsvException(
                                ErrorCsv.WRONG_FIELD_COUNT,
                                lineNumber,
                                "Ожидалось 3 поля, получено " + parts.length);
                    }

                    int profile1 = parseInt(parts[0], lineNumber, "ID первого профиля");
                    int profile2 = parseInt(parts[1], lineNumber, "ID второго профиля");
                    int strength = parseInt(parts[2], lineNumber, "сила связи");

                    if (profile1 <= 0 || profile2 <= 0 || strength < 0) {
                        throw new LoadCsvException(
                                ErrorCsv.INVALID_DATA,
                                lineNumber,
                                "ID профилей должны быть положительными, сила связи не может быть отрицательной");
                    }

                    result.add(new FriendShip(profile1, profile2, strength));
                } catch (LoadCsvException e) {
                    errors.add(e);
                }
            }
        } catch (IOException e) {
            throw new LoadCsvException(
                    ErrorCsv.IO_ERROR,
                    0,
                    "Ошибка чтения файла: " + file,
                    e);
        }

        return new LoadCsvResult<>(result, errors);
    }

    private int parseInt(String value, int lineNumber, String fieldName) throws LoadCsvException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new LoadCsvException(
                    ErrorCsv.BAD_NUMBER,
                    lineNumber,
                    "Поле '" + fieldName + "' должно быть числом");
        }
    }
}
