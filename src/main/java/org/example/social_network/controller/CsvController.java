package org.example.social_network.controller;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.social_network.csv.CsvLoader;
import org.example.social_network.csv.CsvSaver;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.exception.LoadCsvResult;
import org.example.social_network.gui.GenericEntityDialog;
import org.example.social_network.gui.MainApp;
import org.example.social_network.model.*;

import java.io.File;
import java.nio.file.Path;

public final class CsvController {

    public void load(Stage stage, MainApp.Type type,
                     ObservableList<Profile> profiles,
                     ObservableList<Community> communities,
                     ObservableList<DelProfile> deleted,
                     ObservableList<FriendShip> friendships) {
        File file = chooseFile(stage, "Загрузить CSV", false);
        if (file == null) return;

        try {
            loadInto(file.toPath(), type, profiles, communities, deleted, friendships);
        } catch (LoadCsvException e) {
            GenericEntityDialog.showAlert(formatError(e));
        } catch (Exception e) {
            GenericEntityDialog.showAlert("Ошибка загрузки: " + e.getMessage());
        }
    }

    public void save(Stage stage, MainApp.Type type,
                     ObservableList<Profile> profiles,
                     ObservableList<Community> communities,
                     ObservableList<DelProfile> deleted,
                     ObservableList<FriendShip> friendships) {
        File file = chooseFile(stage, "Сохранить CSV", true);
        if (file == null) return;

        try {
            saveFrom(file.toPath(), type, profiles, communities, deleted, friendships);
            GenericEntityDialog.showAlert("Файл сохранён.");
        } catch (Exception e) {
            GenericEntityDialog.showAlert("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void loadInto(Path path, MainApp.Type type,
                          ObservableList<Profile> profiles,
                          ObservableList<Community> communities,
                          ObservableList<DelProfile> deleted,
                          ObservableList<FriendShip> friendships) throws Exception {
        CsvLoader loader = new CsvLoader();
        StringBuilder errors = new StringBuilder();

        switch (type) {
            case PROFILE -> replaceItems(profiles, loader.load(path), errors);
            case COMMUNITY -> replaceItems(communities, loader.load(path), errors);
            case DELETED -> replaceItems(deleted, loader.load(path), errors);
            case FRIENDSHIP -> replaceItems(friendships, loader.load(path), errors);
        }

        if (!errors.isEmpty()) {
            GenericEntityDialog.showAlert("Некоторые строки пропущены:\n" + errors);
        }
    }

    private <T> void replaceItems(ObservableList<T> target, LoadCsvResult<T> result, StringBuilder errors) {
        target.setAll(result.getItems());
        result.getErrors().forEach(error -> errors.append(formatError(error)).append('\n'));
    }

    private void saveFrom(Path path, MainApp.Type type,
                          ObservableList<Profile> profiles,
                          ObservableList<Community> communities,
                          ObservableList<DelProfile> deleted,
                          ObservableList<FriendShip> friendships) throws Exception {
        CsvSaver saver = new CsvSaver();
        switch (type) {
            case PROFILE -> saver.save(profiles, path);
            case COMMUNITY -> saver.save(communities, path);
            case DELETED -> saver.save(deleted, path);
            case FRIENDSHIP -> saver.save(friendships, path);
        }
    }

    private File chooseFile(Stage stage, String title, boolean save) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        if (save) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы", "*.csv"));
            return chooser.showSaveDialog(stage);
        }
        return chooser.showOpenDialog(stage);
    }

    private String formatError(LoadCsvException error) {
        String line = error.getLineNumber() > 0
                ? "Строка " + error.getLineNumber() + " "
                : "";
        return line + "[" + error.getCode() + "]: " + error.getMessage();
    }
}
