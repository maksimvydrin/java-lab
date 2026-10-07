package org.example.socialnetwork.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.socialnetwork.csv.*;
import org.example.socialnetwork.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MainApp extends Application {
    private enum EntityType {
        PROFILE("Профили"),
        DELETED_PROFILE("Удалённые профили"),
        COMMUNITY("Сообщества");

        private final String title;

        EntityType(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final List<Profile> profiles = new ArrayList<>();
    private final List<DeletedProfile> deletedProfiles = new ArrayList<>();
    private final List<Community> communities = new ArrayList<>();

    private final TableView<Object> table = new TableView<>();
    private final ComboBox<EntityType> typeBox = new ComboBox<>();
    private final Button editButton = new Button("Изменить");

    @Override
    public void start(Stage stage) {
        typeBox.getItems().setAll(EntityType.values());
        typeBox.setValue(EntityType.PROFILE);

        Button addButton = new Button("Добавить");
        Button loadButton = new Button("Загрузить CSV");
        Button saveButton = new Button("Сохранить CSV");

        typeBox.setOnAction(event -> refreshTable());
        addButton.setOnAction(event -> addEntity(stage));
        editButton.setOnAction(event -> editEntity(stage));
        loadButton.setOnAction(event -> loadCsv(stage));
        saveButton.setOnAction(event -> saveCsv(stage));

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> updateEditButton());

        HBox controls = new HBox(10, typeBox, addButton, editButton, loadButton, saveButton);
        VBox root = new VBox(10, controls, table);
        root.setStyle("-fx-padding: 12;");

        refreshTable();

        stage.setTitle("Социальная сеть — лабораторная №1");
        stage.setScene(new Scene(root, 900, 560));
        stage.show();
    }

    private void refreshTable() {
        table.getColumns().clear();

        switch (typeBox.getValue()) {
            case PROFILE -> {
                addColumn("ID", Profile::getId);
                addColumn("Имя", Profile::getName);
                addColumn("Город", Profile::getCity);
                addColumn("Год рождения", Profile::getBirthYear);
                table.setItems(FXCollections.observableArrayList(profiles));
            }
            case DELETED_PROFILE -> {
                addColumn("ID", DeletedProfile::getId);
                addColumn("Имя", DeletedProfile::getName);
                addColumn("Город", DeletedProfile::getCity);
                addColumn("Год рождения", DeletedProfile::getBirthYear);
                addColumn("Причина", DeletedProfile::getReason);
                table.setItems(FXCollections.observableArrayList(deletedProfiles));
            }
            case COMMUNITY -> {
                addColumn("ID", Community::getId);
                addColumn("Название", Community::getName);
                addColumn("Город", Community::getCity);
                addColumn("Год создания", Community::getBirthYear);
                addColumn("Администратор", Community::getAdministratorId);
                table.setItems(FXCollections.observableArrayList(communities));
            }
        }

        updateEditButton();
    }

    private <T> void addColumn(String title, java.util.function.Function<T, Object> getter) {
        TableColumn<Object, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(getter.apply((T) cell.getValue())));
        column.setPrefWidth(150);
        table.getColumns().add(column);
    }

    private void updateEditButton() {
        Object selected = table.getSelectionModel().getSelectedItem();
        editButton.setDisable(!(selected instanceof Editable));
    }

    private void addEntity(Stage owner) {
        switch (typeBox.getValue()) {
            case PROFILE -> {
                Profile result = profileDialog(owner, null);
                if (result != null) {
                    profiles.add(result);
                    refreshTable();
                }
            }
            case COMMUNITY -> {
                Community result = communityDialog(owner, null);
                if (result != null) {
                    communities.add(result);
                    refreshTable();
                }
            }
            case DELETED_PROFILE -> showError("Удалённый профиль — read-only. Его нельзя добавить вручную.");
        }
    }

    private void editEntity(Stage owner) {
        Object selected = table.getSelectionModel().getSelectedItem();

        if (selected instanceof Community community) {
            communityDialog(owner, community);
        } else if (selected instanceof Profile profile) {
            profileDialog(owner, profile);
        } else {
            showError("Выбранный объект нельзя редактировать.");
        }

        refreshTable();
    }

    private Profile profileDialog(Stage owner, Profile existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(existing == null ? "Добавить профиль" : "Изменить профиль");

        TextField id = new TextField();
        TextField name = new TextField();
        TextField city = new TextField();
        TextField birthYear = new TextField();

        if (existing != null) {
            id.setText(String.valueOf(existing.getId()));
            name.setText(existing.getName());
            city.setText(existing.getCity());
            birthYear.setText(String.valueOf(existing.getBirthYear()));
        }

        GridPane grid = createGrid();
        grid.addRow(0, new Label("ID:"), id);
        grid.addRow(1, new Label("Имя:"), name);
        grid.addRow(2, new Label("Город:"), city);
        grid.addRow(3, new Label("Год рождения:"), birthYear);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return null;
        }

        try {
            Profile target = existing == null
                    ? new Profile(
                            Integer.parseInt(id.getText().trim()),
                            name.getText().trim(),
                            city.getText().trim(),
                            Integer.parseInt(birthYear.getText().trim()))
                    : existing;

            if (existing != null) {
                target.setId(Integer.parseInt(id.getText().trim()));
                target.setName(name.getText().trim());
                target.setCity(city.getText().trim());
                target.setBirthYear(Integer.parseInt(birthYear.getText().trim()));
            }

            List<String> errors = target.validate();
            if (!errors.isEmpty()) {
                showError(String.join("\n", errors));
                return null;
            }

            return target;
        } catch (NumberFormatException e) {
            showError("ID и год рождения должны быть целыми числами.");
            return null;
        }
    }

    private Community communityDialog(Stage owner, Community existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(existing == null ? "Добавить сообщество" : "Изменить сообщество");

        TextField id = new TextField();
        TextField name = new TextField();
        TextField city = new TextField();
        TextField year = new TextField();
        TextField administratorId = new TextField();

        if (existing != null) {
            id.setText(String.valueOf(existing.getId()));
            name.setText(existing.getName());
            city.setText(existing.getCity());
            year.setText(String.valueOf(existing.getBirthYear()));
            administratorId.setText(String.valueOf(existing.getAdministratorId()));
        }

        GridPane grid = createGrid();
        grid.addRow(0, new Label("ID:"), id);
        grid.addRow(1, new Label("Название:"), name);
        grid.addRow(2, new Label("Город:"), city);
        grid.addRow(3, new Label("Год создания:"), year);
        grid.addRow(4, new Label("ID администратора:"), administratorId);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return null;
        }

        try {
            Community target = existing == null
                    ? new Community(
                            Integer.parseInt(id.getText().trim()),
                            name.getText().trim(),
                            city.getText().trim(),
                            Integer.parseInt(year.getText().trim()),
                            Integer.parseInt(administratorId.getText().trim()))
                    : existing;

            if (existing != null) {
                target.setId(Integer.parseInt(id.getText().trim()));
                target.setName(name.getText().trim());
                target.setCity(city.getText().trim());
                target.setBirthYear(Integer.parseInt(year.getText().trim()));
                target.setAdministratorId(Integer.parseInt(administratorId.getText().trim()));
            }

            List<String> errors = target.validate();
            if (!errors.isEmpty()) {
                showError(String.join("\n", errors));
                return null;
            }

            return target;
        } catch (NumberFormatException e) {
            showError("Числовые поля должны быть целыми числами.");
            return null;
        }
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 10;");
        return grid;
    }

    private void loadCsv(Stage owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Загрузить CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = chooser.showOpenDialog(owner);
        if (file == null) {
            return;
        }

        try {
            Path path = file.toPath();

            switch (typeBox.getValue()) {
                case PROFILE -> {
                    CsvParseResult<Profile> result = ProfileCsv.load(path);
                    profiles.clear();
                    profiles.addAll(result.items());
                    showLoadResult(result.items().size(), result.errors());
                }
                case DELETED_PROFILE -> {
                    CsvParseResult<DeletedProfile> result = DeletedProfileCsv.load(path);
                    deletedProfiles.clear();
                    deletedProfiles.addAll(result.items());
                    showLoadResult(result.items().size(), result.errors());
                }
                case COMMUNITY -> {
                    CsvParseResult<Community> result = CommunityCsv.load(path);
                    communities.clear();
                    communities.addAll(result.items());
                    showLoadResult(result.items().size(), result.errors());
                }
            }

            refreshTable();
        } catch (IOException e) {
            showError("Не удалось прочитать файл: " + e.getMessage());
        }
    }

    private void saveCsv(Stage owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = chooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }

        try {
            Path path = file.toPath();

            switch (typeBox.getValue()) {
                case PROFILE -> ProfileCsv.save(profiles, path);
                case DELETED_PROFILE -> DeletedProfileCsv.save(deletedProfiles, path);
                case COMMUNITY -> CommunityCsv.save(communities, path);
            }

            showInfo("Данные сохранены.");
        } catch (IOException e) {
            showError("Не удалось сохранить файл: " + e.getMessage());
        }
    }

    private void showLoadResult(int loadedCount, List<String> errors) {
        if (errors.isEmpty()) {
            showInfo("CSV загружен. Битых строк не обнаружено.");
        } else {
            showInfo("CSV загружен. Корректных записей: " +
                    loadedCount +
                    ". Пропущено битых строк: " + errors.size() +
                    "\n\n" + String.join("\n", errors));
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
