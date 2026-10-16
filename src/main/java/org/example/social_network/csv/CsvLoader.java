
package org.example.social_network.csv;

import org.example.social_network.exception.ErrorCsv;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.exception.LoadCsvResult;
import org.example.social_network.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvLoader {

    private static final String PROFILE_HEADER =
            "id;name;city;birthYear";
    private static final String COMMUNITY_HEADER =
            "id;name;city;birthYear;adminId";
    private static final String DELETED_HEADER =
            "id;name;city;birthYear;dayDel;delReason";
    private static final String FRIENDSHIP_HEADER =
            "profile1;profile2;strength";

    public <T> LoadCsvResult<T> load(Path file)
            throws LoadCsvException {

        List<T> result = new ArrayList<>();
        List<LoadCsvException> errors = new ArrayList<>();

        try (var reader = Files.newBufferedReader(
                file, StandardCharsets.UTF_8)) {

            String header = reader.readLine();

            if (header == null) {
                throw new LoadCsvException(
                        ErrorCsv.INVALID_DATA,
                        1,
                        "Заголовок отсутствует"
                );
            }

            header = header.trim();

            int expectedFields = switch (header) {
                case PROFILE_HEADER -> 4;
                case COMMUNITY_HEADER -> 5;
                case DELETED_HEADER -> 6;
                case FRIENDSHIP_HEADER -> 3;
                default -> throw new LoadCsvException(
                        ErrorCsv.INVALID_DATA,
                        1,
                        "Неизвестный заголовок: " + header
                );
            };

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                try {
                    String[] parts = line.split(";", -1);

                    if (parts.length != expectedFields) {
                        throw new LoadCsvException(ErrorCsv.WRONG_FIELD_COUNT, lineNumber, "Ожидалось " + expectedFields + " полей, получено "+ +parts.length);
                    }

                    Object item = parseLine(header, parts, lineNumber);

                    @SuppressWarnings("unchecked")
                    T typedItem = (T) item;

                    result.add(typedItem);

                } catch (LoadCsvException e) {
                    errors.add(e);
                } catch (RuntimeException e) {
                    errors.add(new LoadCsvException(
                            ErrorCsv.INVALID_DATA,
                            lineNumber,
                            "Некорректные данные: " + e.getMessage(),
                            e
                    ));
                }
            }

        } catch (IOException e) {
            throw new LoadCsvException(ErrorCsv.IO_ERROR, 0, "Ошибка чтения файла: " + file, e);
        }

        return new LoadCsvResult<>(result, errors);
    }

    private Object parseLine(
            String header,
            String[] parts,
            int lineNumber
    ) throws LoadCsvException {

        if (PROFILE_HEADER.equals(header)) {
            Profile profile = new Profile(
                    parseInt(parts[0], lineNumber, "ID"),
                    parts[1],
                    parts[2],
                    parseInt(parts[3], lineNumber, "Год рождения")
            );

            validateModel(profile, lineNumber);
            return profile;
        }

        if (COMMUNITY_HEADER.equals(header)) {
            Community community = new Community(
                    parseInt(parts[0], lineNumber, "ID"),
                    parts[1],
                    parts[2],
                    parseInt(parts[3], lineNumber, "Год создания"),
                    parseInt(parts[4], lineNumber, "ID администратора")
            );

            validateModel(community, lineNumber);
            return community;
        }

        if (DELETED_HEADER.equals(header)) {
            DelProfile profile = new DelProfile(
                    parseInt(parts[0], lineNumber, "ID"),
                    parts[1],
                    parts[2],
                    parseInt(parts[3], lineNumber, "Год рождения"),
                    parseInt(parts[4], lineNumber, "Дата удаления"),
                    parts[5]
            );

            validateModel(profile, lineNumber);
            return profile;
        }

        if (FRIENDSHIP_HEADER.equals(header)) {
            FriendShip friendship = new FriendShip(
                    parseInt(parts[0], lineNumber, "ID первого профиля"),
                    parseInt(parts[1], lineNumber, "ID второго профиля"),
                    parseInt(parts[2], lineNumber, "Сила связи")
            );

            validateModel(friendship, lineNumber);
            return friendship;
        }

        throw new LoadCsvException(ErrorCsv.INVALID_DATA, lineNumber, "Неизвестный тип данных");
    }

    private void validateModel(
            Editable item,
            int lineNumber
    ) throws LoadCsvException {

        List<String> validationErrors = item.validate();

        if (!validationErrors.isEmpty()) {
            throw new LoadCsvException(ErrorCsv.INVALID_DATA, lineNumber, String.join("; ", validationErrors)
            );
        }
    }

    private int parseInt(
            String value,
            int lineNumber,
            String fieldName
    ) throws LoadCsvException {

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new LoadCsvException(ErrorCsv.BAD_NUMBER, lineNumber, "Поле '" + fieldName + "' должно быть целым числом", e
            );
        }
    }
}
