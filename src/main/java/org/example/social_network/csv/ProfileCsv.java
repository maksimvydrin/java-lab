package org.example.socialnetwork.csv;

import org.example.socialnetwork.model.Profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProfileCsv {
    private static final String HEADER = "id;name;city;birthYear";

    private ProfileCsv() {
    }

    public static CsvParseResult<Profile> load(Path file) throws IOException {
        List<Profile> items = new ArrayList<>();
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
                if (parts.length != 4) {
                    errors.add("Строка " + lineNumber + ": ожидалось 4 поля.");
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    int birthYear = Integer.parseInt(parts[3].trim());
                    Profile profile = new Profile(id, parts[1].trim(), parts[2].trim(), birthYear);
                    List<String> validation = profile.validate();

                    if (validation.isEmpty()) {
                        items.add(profile);
                    } else {
                        errors.add("Строка " + lineNumber + ": " + String.join(" ", validation));
                    }
                } catch (NumberFormatException e) {
                    errors.add("Строка " + lineNumber + ": ID и год рождения должны быть числами.");
                }
            }
        }

        return new CsvParseResult<>(items, errors);
    }

    public static void save(List<Profile> profiles, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();

            for (Profile profile : profiles) {
                writer.write(Integer.toString(profile.getId()));
                writer.write(';');
                writer.write(clean(profile.getName()));
                writer.write(';');
                writer.write(clean(profile.getCity()));
                writer.write(';');
                writer.write(Integer.toString(profile.getBirthYear()));
                writer.newLine();
            }
        }
    }

    private static String clean(String value) {
        return value == null ? "" : value.replace(";", ",").replace("\r", " ").replace("\n", " ");
    }
}
