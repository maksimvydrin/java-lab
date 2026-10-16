
package org.example.social_network.controller;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.social_network.csv.CsvLoader;
import org.example.social_network.csv.CsvSaver;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.exception.LoadCsvResult;
import org.example.social_network.gui.GenericDialog;
import org.example.social_network.gui.MainApp;
import org.example.social_network.model.*;

import java.io.File;
import java.nio.file.Path;

public final class CsvController {

    public void load(
            Stage stage,
            MainApp.Type type,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<DelProfile> deleted,
            ObservableList<FriendShip> friendships
    ) {
        File file = chooseFile(stage, "Загрузить CSV", false);

        if (file == null) {
            return;
        }

        try {
            loadInto(
                    file.toPath(), type,
                    profiles, communities, deleted, friendships
            );
        } catch (LoadCsvException e) {
            GenericDialog.showAlert(formatError(e));
        } catch (Exception e) {
            GenericDialog.showAlert(
                    "Ошибка загрузки: " + e.getMessage()
            );
        }
    }

    public void save(
            Stage stage,
            MainApp.Type type,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<DelProfile> deleted,
            ObservableList<FriendShip> friendships
    ) {
        File file = chooseFile(stage, "Сохранить CSV", true);

        if (file == null) {
            return;
        }

        try {
            saveFrom(
                    file.toPath(), type,
                    profiles, communities, deleted, friendships
            );
            GenericDialog.showAlert("Файл сохранён.");
        } catch (Exception e) {
            GenericDialog.showAlert(
                    "Ошибка сохранения: " + e.getMessage()
            );
        }
    }

    private void loadInto(
            Path path,
            MainApp.Type type,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<DelProfile> deleted,
            ObservableList<FriendShip> friendships
    ) throws Exception {

        CsvLoader loader = new CsvLoader();
        StringBuilder errors = new StringBuilder();

        switch (type) {
            case PROFILE ->
                    replaceItems(profiles, loader.load(path), errors);
            case COMMUNITY ->
                    replaceItems(communities, loader.load(path), errors);
            case DELETED ->
                    replaceItems(deleted, loader.load(path), errors);
            case FRIENDSHIP ->
                    replaceItems(friendships, loader.load(path), errors);
        }

        if (!errors.isEmpty()) {
            GenericDialog.showAlert(
                    "Некоторые строки пропущены:\n" + errors
            );
        }
    }

    private <T> void replaceItems(
            ObservableList<T> target,
            LoadCsvResult<T> result,
            StringBuilder errors
    ) {
        target.setAll(result.getItems());

        result.getErrors().forEach(error ->
                errors.append(formatError(error)).append('\n')
        );
    }

    private void saveFrom(
            Path path,
            MainApp.Type type,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<DelProfile> deleted,
            ObservableList<FriendShip> friendships
    ) {
        CsvSaver saver = new CsvSaver();

        switch (type) {
            case PROFILE -> saver.save(
                    profiles, path, "id;name;city;birthYear"
            );

            case COMMUNITY -> saver.save(
                    communities, path,
                    "id;name;city;birthYear;adminId"
            );

            case DELETED -> saver.save(
                    deleted, path,
                    "id;name;city;birthYear;dayDel;delReason"
            );

            case FRIENDSHIP -> saver.save(
                    friendships, path,
                    "profile1;profile2;strength"
            );
        }
    }

    private File chooseFile(
            Stage stage,
            String title,
            boolean save
    ) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV файлы", "*.csv"
                )
        );

        return save
                ? chooser.showSaveDialog(stage)
                : chooser.showOpenDialog(stage);
    }

    private String formatError(LoadCsvException error) {
        String line = error.getLineNumber() > 0
                ? "Строка " + error.getLineNumber() + " "
                : "";

        return line + "[" + error.getCode() + "]: "
                + error.getMessage();
    }
}
