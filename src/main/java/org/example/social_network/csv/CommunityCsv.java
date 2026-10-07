package org.example.socialnetwork.csv;

import org.example.socialnetwork.model.Community;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CommunityCsv {
    private static final String HEADER = "id;name;city;birthYear;administratorId";

    private CommunityCsv() {
    }

    public static CsvParseResult<Community> load(Path file) throws IOException {
        List<Community> items = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length != 5) {
                    errors.add("Строка " + lineNumber + ": ожидалось 5 полей.");
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    int birthYear = Integer.parseInt(parts[3].trim());
                    int administratorId = Integer.parseInt(parts[4].trim());

                    Community community = new Community(
                            id, parts[1].trim(), parts[2].trim(), birthYear, administratorId);

                    List<String> validation = community.validate();
                    if (validation.isEmpty()) {
                        items.add(community);
                    } else {
                        errors.add("Строка " + lineNumber + ": " + String.join(" ", validation));
                    }
                } catch (NumberFormatException e) {
                    errors.add("Строка " + lineNumber + ": числовые поля имеют неверный формат.");
                }
            }
        }

        return new CsvParseResult<>(items, errors);
    }

    public static void save(List<Community> communities, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();

            for (Community community : communities) {
                writer.write(Integer.toString(community.getId()));
                writer.write(';');
                writer.write(clean(community.getName()));
                writer.write(';');
                writer.write(clean(community.getCity()));
                writer.write(';');
                writer.write(Integer.toString(community.getBirthYear()));
                writer.write(';');
                writer.write(Integer.toString(community.getAdministratorId()));
                writer.newLine();
            }
        }
    }

    private static String clean(String value) {
        return value == null ? "" : value.replace(";", ",").replace("\r", " ").replace("\n", " ");
    }
}
