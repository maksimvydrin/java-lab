package org.example.social_network.gui;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.social_network.csv.CommunityCsvLoader;
import org.example.social_network.csv.DelProfileCsvLoader;
import org.example.social_network.csv.ProfileCsvLoader;
import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.Editable;
import org.example.social_network.model.Profile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MainApp extends Application {
    private enum Type {
        PROFILE("Профили"), COMMUNITY("Сообщества"), DELETED("Удалённые профили");
        final String title;
        Type(String title) { this.title = title; }
        @Override public String toString() { return title; }
    }

    private final TableView<Object> table = new TableView<>();
    private final ComboBox<Type> typeBox = new ComboBox<>();
    private final Button editButton = new Button("Изменить");

    private final ObservableList<Profile> profiles = FXCollections.observableArrayList();
    private final ObservableList<Community> communities = FXCollections.observableArrayList();
    private final ObservableList<DelProfile> deleted = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        typeBox.getItems().addAll(Type.values());
        typeBox.setValue(Type.PROFILE);

        Button addButton = new Button("Добавить");
        Button loadButton = new Button("Загрузить CSV");
        Button saveButton = new Button("Сохранить CSV");

        typeBox.setOnAction(e -> refreshTable());
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, value) -> updateEditButton());
        addButton.setOnAction(e -> addEntity());
        editButton.setOnAction(e -> editEntity());
        loadButton.setOnAction(e -> loadCsv(stage));
        saveButton.setOnAction(e -> saveCsv(stage));

        HBox controls = new HBox(10, new Label("Тип:"), typeBox, addButton, editButton, loadButton, saveButton);
        controls.setPadding(new Insets(10));

        BorderPane root = new BorderPane(table);
        root.setTop(controls);
        BorderPane.setMargin(table, new Insets(0, 10, 10, 10));

        refreshTable();
        stage.setTitle("Лабораторная работа №1 — Социальная сеть");
        stage.setScene(new Scene(root, 900, 520));
        stage.show();
    }

    private void refreshTable() {
        table.getColumns().clear();
        Type type = typeBox.getValue();

        addColumn("ID", v -> Integer.toString(idOf(v)));
        addColumn(type == Type.COMMUNITY ? "Название" : "Имя", v -> nameOf(v));
        addColumn("Город", v -> cityOf(v));
        addColumn(type == Type.COMMUNITY ? "Год" : "Год рождения", v -> Integer.toString(yearOf(v)));

        if (type == Type.COMMUNITY) {
            addColumn("ID администратора", v -> Integer.toString(((Community) v).getAdministratorId()));
            table.setItems(asObjects(communities));
        } else if (type == Type.DELETED) {
            addColumn("Причина удаления", v -> ((DelProfile) v).getDelReason());
            table.setItems(asObjects(deleted));
        } else {
            table.setItems(asObjects(profiles));
        }
        updateEditButton();
    }

    private void addColumn(String title, java.util.function.Function<Object, String> value) {
        TableColumn<Object, String> column = new TableColumn<>(title);
        column.setCellValueFactory(c -> new SimpleStringProperty(value.apply(c.getValue())));
        column.setPrefWidth(150);
        table.getColumns().add(column);
    }

    private <T> ObservableList<Object> asObjects(ObservableList<T> source) {
        ObservableList<Object> result = FXCollections.observableArrayList();
        result.addAll(source);
        return result;
    }

    private void updateEditButton() {
        Object selected = table.getSelectionModel().getSelectedItem();
        editButton.setDisable(!(selected instanceof Editable));
    }

    private void addEntity() {
        Type type = typeBox.getValue();
        if (type == Type.DELETED) {
            showError("Добавление запрещено", "Read-only объекты можно только загрузить из CSV.");
            return;
        }

        Object entity = type == Type.PROFILE ? profileDialog(null) : communityDialog(null);
        if (entity == null) return;

        if (idExists(idOf(entity))) {
            showError("Ошибка", "Объект с таким ID уже существует.");
            return;
        }

        if (entity instanceof Profile p) profiles.add(p);
        else if (entity instanceof Community c) communities.add(c);
        refreshTable();
    }

    private void editEntity() {
        Object selected = table.getSelectionModel().getSelectedItem();
        if (!(selected instanceof Editable)) return;

        Object edited = selected instanceof Profile p ? profileDialog(p) : communityDialog((Community) selected);
        if (edited == null) return;

        if (selected instanceof Profile old && edited instanceof Profile fresh) {
            old.setId(fresh.getId());
            old.setName(fresh.getName());
            old.setCity(fresh.getCity());
            old.setBirthYear(fresh.getBirthYear());
        } else if (selected instanceof Community old && edited instanceof Community fresh) {
            old.setName(fresh.getName());
            old.setCity(fresh.getCity());
            old.setBirthYear(fresh.getBirthYear());
            old.setAdministratorId(fresh.getAdministratorId());
        }
        refreshTable();
    }

    private Profile profileDialog(Profile source) {
        TextField id = field(source == null ? "" : String.valueOf(source.getId()));
        TextField name = field(source == null ? "" : source.getName());
        TextField city = field(source == null ? "" : source.getCity());
        TextField year = field(source == null ? "" : String.valueOf(source.getBirthYear()));
        GridPane grid = grid("ID", id, "Имя", name, "Город", city, "Год рождения", year);

        while (true) {
            if (!confirm(source == null ? "Добавить профиль" : "Изменить профиль", grid)) return null;
            try {
                Profile p = new Profile(parseInt(id, "ID"), name.getText().trim(), city.getText().trim(), parseInt(year, "Год рождения"));
                if (validate(p)) return p;
            } catch (IllegalArgumentException e) { showError("Ошибка ввода", e.getMessage()); }
        }
    }

    private Community communityDialog(Community source) {
        TextField id = field(source == null ? "" : String.valueOf(source.getId()));
        if (source != null) id.setDisable(true);
        TextField name = field(source == null ? "" : source.getName());
        TextField city = field(source == null ? "" : source.getCity());
        TextField year = field(source == null ? "" : String.valueOf(source.getBirthYear()));
        TextField admin = field(source == null ? "" : String.valueOf(source.getAdministratorId()));
        GridPane grid = grid("ID", id, "Название", name, "Город", city, "Год", year, "ID администратора", admin);

        while (true) {
            if (!confirm(source == null ? "Добавить сообщество" : "Изменить сообщество", grid)) return null;
            try {
                Community c = new Community(parseInt(id, "ID"), name.getText().trim(), city.getText().trim(),
                        parseInt(year, "Год"), parseInt(admin, "ID администратора"));
                if (validate(c)) return c;
            } catch (IllegalArgumentException e) { showError("Ошибка ввода", e.getMessage()); }
        }
    }

    private boolean validate(Editable entity) {
        List<String> errors = entity.validate();
        if (errors.isEmpty()) return true;
        showError("Некорректные данные", String.join("\n", errors));
        return false;
    }

    private void loadCsv(Stage stage) {
        Path file = chooseFile(stage, false);
        if (file == null) return;
        try {
            switch (typeBox.getValue()) {
                case PROFILE -> {
                    profiles.setAll(new ProfileCsvLoader().load(file));
                    showInfo("Загрузка", "Загружено профилей: " + profiles.size());
                }
                case COMMUNITY -> {
                    communities.setAll(new CommunityCsvLoader().load(file));
                    showInfo("Загрузка", "Загружено сообществ: " + communities.size());
                }
                case DELETED -> {
                    deleted.setAll(new DelProfileCsvLoader().load(file));
                    showInfo("Загрузка", "Загружено удалённых профилей: " + deleted.size());
                }
            }
            refreshTable();
        } catch (RuntimeException e) {
            showError("Ошибка загрузки", e.getMessage());
        }
    }

    private void saveCsv(Stage stage) {
        Path file = chooseFile(stage, true);
        if (file == null) return;
        try {
            switch (typeBox.getValue()) {
                case PROFILE -> saveProfiles(file);
                case COMMUNITY -> saveCommunities(file);
                case DELETED -> saveDeleted(file);
            }
            showInfo("Сохранение", "Файл успешно сохранён.");
        } catch (IOException e) {
            showError("Ошибка сохранения", e.getMessage());
        }
    }

    private void saveProfiles(Path file) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file, Charset.forName("windows-1251"))) {
            w.write("id;name;city;birthYear\n");
            for (Profile p : profiles)
                w.write(p.getId() + ";" + clean(p.getName()) + ";" + clean(p.getCity()) + ";" + p.getBirthYear() + "\n");
        }
    }

    private void saveCommunities(Path file) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write("id;name;city;birthYear;adminId\n");
            for (Community c : communities)
                w.write(c.getId() + ";" + clean(c.getName()) + ";" + clean(c.getCity()) + ";" + c.getBirthYear() + ";" + c.getAdministratorId() + "\n");
        }
    }

    private void saveDeleted(Path file) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write("id;name;city;birthYear;delReason\n");
            for (DelProfile p : deleted)
                w.write(p.getId() + ";" + clean(p.getName()) + ";" + clean(p.getCity()) + ";" + p.getBirthYear() + ";" + clean(p.getDelReason()) + "\n");
        }
    }

    private Path chooseFile(Stage stage, boolean save) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        java.io.File f = save ? chooser.showSaveDialog(stage) : chooser.showOpenDialog(stage);
        return f == null ? null : f.toPath();
    }

    private boolean idExists(int id) {
        return profiles.stream().anyMatch(p -> p.getId() == id)
                || communities.stream().anyMatch(c -> c.getId() == id)
                || deleted.stream().anyMatch(p -> p.getId() == id);
    }

    private static int idOf(Object v) {
        if (v instanceof Profile p) return p.getId();
        if (v instanceof Community c) return c.getId();
        return ((DelProfile) v).getId();
    }

    private static String nameOf(Object v) {
        if (v instanceof Profile p) return p.getName();
        if (v instanceof Community c) return c.getName();
        return ((DelProfile) v).getName();
    }

    private static String cityOf(Object v) {
        if (v instanceof Profile p) return p.getCity();
        if (v instanceof Community c) return c.getCity();
        return ((DelProfile) v).getCity();
    }

    private static int yearOf(Object v) {
        if (v instanceof Profile p) return p.getBirthYear();
        if (v instanceof Community c) return c.getBirthYear();
        return ((DelProfile) v).getBirthYear();
    }

    private static TextField field(String text) { return new TextField(text); }

    private static int parseInt(TextField field, String name) {
        try { return Integer.parseInt(field.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(name + " должен быть целым числом."); }
    }

    private static GridPane grid(Object... values) {
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(10));
        for (int i = 0; i < values.length; i += 2) {
            grid.add(new Label(values[i] + ":"), 0, i / 2);
            grid.add((TextField) values[i + 1], 1, i / 2);
        }
        return grid;
    }

    private static boolean confirm(String title, GridPane content) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        return dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private static String clean(String s) {
        return s == null ? "" : s.replace(";", ",").replace("\r", " ").replace("\n", " ");
    }

    private static void showError(String title, String text) { alert(Alert.AlertType.ERROR, title, text); }
    private static void showInfo(String title, String text) { alert(Alert.AlertType.INFORMATION, title, text); }
    private static void alert(Alert.AlertType type, String title, String text) {
        Alert a = new Alert(type, text, ButtonType.OK); a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
