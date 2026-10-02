package org.example.social_network.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.social_network.csv.*;
import org.example.social_network.model.*;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.exception.LoadCsvResult;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class MainApp extends javafx.application.Application {

    enum Type {
        PROFILE("Профили"),
        COMMUNITY("Сообщества"),
        DELETED("Удалённые профили"),
        FRIENDSHIP("Дружба");

        final String title;

        Type(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final ObservableList<Profile> profiles =
            FXCollections.observableArrayList();

    private final ObservableList<Community> communities =
            FXCollections.observableArrayList();

    private final ObservableList<DelProfile> deleted =
            FXCollections.observableArrayList();

    private final ObservableList<FriendShip> friendships =
            FXCollections.observableArrayList();

    private final TableView<Object> table = new TableView<>();
    private final ComboBox<Type> typeBox = new ComboBox<>();
    private final Button editButton = new Button("Изменить");

    @Override
    public void start(Stage stage) {
        typeBox.getItems().addAll(Type.values());
        typeBox.setValue(Type.PROFILE);

        Button addButton = new Button("Добавить");
        Button loadButton = new Button("Загрузить CSV");
        Button saveButton = new Button("Сохранить CSV");

        typeBox.setOnAction(e -> refreshTable());
        addButton.setOnAction(e -> addEntity(stage));
        editButton.setOnAction(e -> editEntity(stage));
        loadButton.setOnAction(e -> loadCsv(stage));
        saveButton.setOnAction(e -> saveCsv(stage));

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> updateEditButton());

        HBox controls = new HBox(10, typeBox, addButton, editButton, loadButton, saveButton);

        VBox root = new VBox(10, controls, table);
        root.setPadding(new Insets(10));

        refreshTable();

        stage.setTitle("Социальная сеть");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    private void refreshTable() {
        table.getColumns().clear();

        switch (typeBox.getValue()) {
            case PROFILE:
                addColumn("ID", Profile::getId);
                addColumn("Имя", Profile::getName);
                addColumn("Город", Profile::getCity);
                addColumn("Год рождения", Profile::getBirthYear);
                table.setItems(FXCollections.observableArrayList(profiles));
                break;

            case COMMUNITY:
                addColumn("ID", Community::getId);
                addColumn("Название", Community::getName);
                addColumn("Город", Community::getCity);
                addColumn("Год создания", Community::getBirthYear);
                addColumn("Администратор", Community::getAdministratorId);
                table.setItems(FXCollections.observableArrayList(communities));
                break;

            case DELETED:
                addColumn("ID", DelProfile::getId);
                addColumn("Имя", DelProfile::getName);
                addColumn("Город", DelProfile::getCity);
                addColumn("Год рождения", DelProfile::getBirthYear);
                addColumn("Причина", DelProfile::getDelReason);
                table.setItems(FXCollections.observableArrayList(deleted));
                break;

            case FRIENDSHIP:
                addColumn("Профиль 1", FriendShip::getProfile1);
                addColumn("Профиль 2", FriendShip::getProfile2);
                addColumn("Сила связи", FriendShip::getStrength);
                table.setItems(FXCollections.observableArrayList(friendships));
                break;
        }

        updateEditButton();
    }

    private <T> void addColumn(
            String title,
            java.util.function.Function<T, Object> getter) {

        TableColumn<Object, Object> column = new TableColumn<>(title);

        column.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(getter.apply((T) cell.getValue())));
        table.getColumns().add(column);
    }

    private void updateEditButton() {
        Object selected = table.getSelectionModel().getSelectedItem();

        if (selected instanceof Editable) {
            editButton.setDisable(false);
        } else {
            editButton.setDisable(true);
        }
    }

    private void addEntity(Stage stage) {
        if (typeBox.getValue() == Type.DELETED) {
            alert("Удалённые профили нельзя добавлять.");
            return;
        }

        if (typeBox.getValue() == Type.PROFILE) {
            Profile profile = profileDialog(stage, null);

            if (profile != null) {
                profiles.add(profile);
                refreshTable();
            }
        } else if (typeBox.getValue() == Type.COMMUNITY) {
            Community community = communityDialog(stage, null);

            if (community != null) {
                communities.add(community);
                refreshTable();
            }
        } else {
            FriendShip friendship = friendshipDialog(stage, null);
            if (friendship != null) {
                friendships.add(friendship);
                refreshTable();
            }
        }
    }

    private void editEntity(Stage stage) {
        Object selected = table.getSelectionModel().getSelectedItem();

        if (selected instanceof Community) {
            Community community = (Community) selected;
            Community result = communityDialog(stage, community);

            if (result != null) {
                refreshTable();
            }
        } else if (selected instanceof Profile) {
            Profile profile = (Profile) selected;
            Profile result = profileDialog(stage, profile);

            if (result != null) {
                refreshTable();
            }
        } else if (selected instanceof FriendShip) {
            FriendShip friendship = (FriendShip) selected;
            FriendShip result = friendshipDialog(stage, friendship);

            if (result != null) {
                refreshTable();
            }
        }
    }

    private Profile profileDialog(Stage owner, Profile profile) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);

        if (profile == null) {
            dialog.setTitle("Добавить профиль");
        } else {
            dialog.setTitle("Изменить профиль");
        }

        TextField id = new TextField();
        TextField name = new TextField();
        TextField city = new TextField();
        TextField birthYear = new TextField();

        if (profile != null) {
            id.setText(String.valueOf(profile.getId()));
            name.setText(profile.getName());
            city.setText(profile.getCity());
            birthYear.setText(String.valueOf(profile.getBirthYear()));
        }

        GridPane grid = grid();

        grid.add(new Label("ID:"), 0, 0);
        grid.add(id, 1, 0);

        grid.add(new Label("Имя:"), 0, 1);
        grid.add(name, 1, 1);

        grid.add(new Label("Город:"), 0, 2);
        grid.add(city, 1, 2);

        grid.add(new Label("Год рождения:"), 0, 3);
        grid.add(birthYear, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        ButtonType resultButton = dialog.showAndWait().orElse(ButtonType.CANCEL);

        if (resultButton != ButtonType.OK) {
            return null;
        }

        try {
            int profileId = Integer.parseInt(id.getText());
            String profileName = name.getText();
            String profileCity = city.getText();
            int profileBirthYear = Integer.parseInt(birthYear.getText());

            Profile result;

            if (profile == null) {
                result = new Profile(
                        profileId,
                        profileName,
                        profileCity,
                        profileBirthYear
                );
            } else {
                result = profile;
                result.setId(profileId);
                result.setName(profileName);
                result.setCity(profileCity);
                result.setBirthYear(profileBirthYear);
            }

            List<String> errors = result.validate();

            if (!errors.isEmpty()) {
                alert(String.join("\n", errors));
                return null;
            }

            return result;

        } catch (NumberFormatException e) {
            alert("ID и год рождения должны быть числами.");
            return null;
        }
    }

    private Community communityDialog(Stage owner, Community community) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);

        if (community == null) {
            dialog.setTitle("Добавить сообщество");
        } else {
            dialog.setTitle("Изменить сообщество");
        }

        TextField id = new TextField();
        TextField name = new TextField();
        TextField city = new TextField();
        TextField birthYear = new TextField();
        TextField adminId = new TextField();

        if (community != null) {
            id.setText(String.valueOf(community.getId()));
            name.setText(community.getName());
            city.setText(community.getCity());
            birthYear.setText(String.valueOf(community.getBirthYear()));
            adminId.setText(String.valueOf(community.getAdministratorId()));
        }

        id.setDisable(community != null);

        GridPane grid = grid();

        grid.add(new Label("ID:"), 0, 0);
        grid.add(id, 1, 0);

        grid.add(new Label("Название:"), 0, 1);
        grid.add(name, 1, 1);

        grid.add(new Label("Город:"), 0, 2);
        grid.add(city, 1, 2);

        grid.add(new Label("Год создания:"), 0, 3);
        grid.add(birthYear, 1, 3);

        grid.add(new Label("Администратор:"), 0, 4);
        grid.add(adminId, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ButtonType resultButton = dialog.showAndWait().orElse(ButtonType.CANCEL);

        if (resultButton != ButtonType.OK) {
            return null;
        }

        try {
            int communityId = Integer.parseInt(id.getText());
            String communityName = name.getText();
            String communityCity = city.getText();
            int communityBirthYear = Integer.parseInt(birthYear.getText());
            int administratorId = Integer.parseInt(adminId.getText());

            Community result;

            if (community == null) {
                result = new Community(
                        communityId,
                        communityName,
                        communityCity,
                        communityBirthYear,
                        administratorId
                );
            } else {
                result = community;
                result.setName(communityName);
                result.setCity(communityCity);
                result.setBirthYear(communityBirthYear);
                result.setAdministratorId(administratorId);
            }

            List<String> errors = result.validate();

            if (!errors.isEmpty()) {
                alert(String.join("\n", errors));
                return null;
            }

            return result;

        } catch (NumberFormatException e) {
            alert("Числовые поля должны содержать числа.");
            return null;
        }
    }

    private FriendShip friendshipDialog(Stage owner, FriendShip friendship) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(friendship == null ? "Добавить дружбу" : "Изменить дружбу");

        TextField profile1 = new TextField();
        TextField profile2 = new TextField();
        TextField strength = new TextField();

        if (friendship != null) {
            profile1.setText(String.valueOf(friendship.getProfile1()));
            profile2.setText(String.valueOf(friendship.getProfile2()));
            strength.setText(String.valueOf(friendship.getStrength()));
            profile1.setDisable(true);
            profile2.setDisable(true);
        }

        GridPane grid = grid();
        grid.add(new Label("Профиль 1:"), 0, 0);
        grid.add(profile1, 1, 0);
        grid.add(new Label("Профиль 2:"), 0, 1);
        grid.add(profile2, 1, 1);
        grid.add(new Label("Сила связи:"), 0, 2);
        grid.add(strength, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ButtonType resultButton = dialog.showAndWait().orElse(ButtonType.CANCEL);
        if (resultButton != ButtonType.OK) {
            return null;
        }

        try {
            int id1 = Integer.parseInt(profile1.getText());
            int id2 = Integer.parseInt(profile2.getText());
            int value = Integer.parseInt(strength.getText());

            FriendShip result;
            if (friendship == null) {
                result = new FriendShip(id1, id2, value);
            } else {
                result = friendship;
                result.setStrength(value);
            }

            List<String> errors = result.validate();
            if (!errors.isEmpty()) {
                alert(String.join("\n", errors));
                return null;
            }

            return result;
        } catch (NumberFormatException e) {
            alert("ID профилей и сила связи должны быть числами.");
            return null;
        }
    }

    private void loadCsv(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Загрузить CSV");

        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Path path = file.toPath();
            StringBuilder errorsText = new StringBuilder();

            switch (typeBox.getValue()) {
                case PROFILE -> {
                    LoadCsvResult<Profile> result = new ProfileCsvLoader().load(path);
                    profiles.setAll(result.getItems());
                    appendErrors(errorsText, result);
                }
                case COMMUNITY -> {
                    LoadCsvResult<Community> result = new CommunityCsvLoader().load(path);
                    communities.setAll(result.getItems());
                    appendErrors(errorsText, result);
                }
                case DELETED -> {
                    LoadCsvResult<DelProfile> result = new DelProfileCsvLoader().load(path);
                    deleted.setAll(result.getItems());
                    appendErrors(errorsText, result);
                }
                case FRIENDSHIP -> {
                    LoadCsvResult<FriendShip> result = new FriendShipCsvLoader().load(path);
                    friendships.setAll(result.getItems());
                    appendErrors(errorsText, result);
                }
            }

            refreshTable();

            if (errorsText.length() > 0) {
                alert("Некоторые строки пропущены:\n" + errorsText);
            }
        } catch (LoadCsvException e) {
            alert(formatError(e));
        } catch (Exception e) {
            alert("Ошибка загрузки: " + e.getMessage());
        }
    }

    private void appendErrors(StringBuilder builder, LoadCsvResult<?> result) {
        result.getErrors().forEach(error ->
                builder.append(formatError(error)).append("\n"));
    }

    private String formatError(LoadCsvException e) {
        if (e.getLineNumber() > 0) {
            return "Строка " + e.getLineNumber() + " [" + e.getCode() + "]: " + e.getMessage();
        }
        return "[" + e.getCode() + "]: " + e.getMessage();
    }

    private void saveCsv(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить CSV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файлы", "*.csv"));

        File file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Path path = file.toPath();
            switch (typeBox.getValue()) {
                case PROFILE -> new ProfileCsvSaver().save(profiles, path);
                case COMMUNITY -> new CommunityCsvSaver().save(communities, path);
                case DELETED -> new DelProfileCsvSaver().save(deleted, path);
                case FRIENDSHIP -> new FriendShipCsvSaver().save(friendships, path);
            }
            alert("Файл сохранён.");
        } catch (Exception e) {
            alert("Ошибка сохранения: " + e.getMessage());
        }
    }

    private GridPane grid() {
        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        return grid;
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Социальная сеть");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}