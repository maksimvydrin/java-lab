package org.example.social_network.gui;

import org.example.social_network.csv.CommunityCsvLoader;
import org.example.social_network.csv.DelProfileCsvLoader;
import org.example.social_network.model.FriendShip;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.example.social_network.csv.ProfileCsvLoader;
import org.example.social_network.csv.ProfileCsvSaver;
import org.example.social_network.graph.Graph;
import org.example.social_network.graph.GraphAlgorithms;
import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.Profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private final TableView<Profile> profileTable =
            new TableView<>();

    private final TableView<Community> communityTable =
            new TableView<>();

    private final TableView<DelProfile> delProfileTable =
            new TableView<>();

    private final Graph graph = new Graph();

    private final GraphAlgorithms graphAlgorithms =
            new GraphAlgorithms();

    private final ComboBox<String> typeBox =
            new ComboBox<>();

    private final Button addButton =
            new Button("Добавить");

    private final Button editButton =
            new Button("Изменить");

    private final Button loadButton =
            new Button("Загрузить");

    private final Button saveButton =
            new Button("Сохранить");

    private final Button bfsButton =
            new Button("BFS");

    private final Button dijkstraButton =
            new Button("Dijkstra");

    @Override
    public void start(Stage stage) {

        Profile profile1 =
                new Profile(
                        1,
                        "Иван",
                        "Москва",
                        2000
                );

        Profile profile2 =
                new Profile(
                        2,
                        "Петр",
                        "Казань",
                        1999
                );

        Profile profile3 =
                new Profile(
                        3,
                        "Анна",
                        "Санкт-Петербург",
                        2001
                );

        profileTable.setItems(
                FXCollections.observableArrayList(
                        profile1,
                        profile2,
                        profile3
                )
        );

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);

        graph.addFriendShip(new FriendShip(1, 2, 5));

        graph.addFriendShip(new FriendShip(2, 3, 3));

        TableColumn<Profile, Number> profileIdColumn =
                new TableColumn<>("ID");

        profileIdColumn.setCellValueFactory(
                cellData ->
                        new SimpleIntegerProperty(
                                cellData.getValue().getId()
                        )
        );

        TableColumn<Profile, String> profileNameColumn =
                new TableColumn<>("Имя");

        profileNameColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getName()
                        )
        );

        TableColumn<Profile, String> profileCityColumn =
                new TableColumn<>("Город");

        profileCityColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getCity()
                        )
        );

        TableColumn<Profile, Number> profileYearColumn =
                new TableColumn<>("Год рождения");

        profileYearColumn.setCellValueFactory(
                cellData ->
                        new SimpleIntegerProperty(
                                cellData.getValue().getBirthYear()
                        )
        );

        profileTable.getColumns().addAll(
                profileIdColumn,
                profileNameColumn,
                profileCityColumn,
                profileYearColumn
        );

        TableColumn<Community, Number> communityIdColumn =
                new TableColumn<>("ID");

        communityIdColumn.setCellValueFactory(
                cellData ->
                        new SimpleIntegerProperty(
                                cellData.getValue().getId()
                        )
        );

        TableColumn<Community, String> communityNameColumn =
                new TableColumn<>("Название");

        communityNameColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getName()
                        )
        );

        TableColumn<Community, String> communityCityColumn =
                new TableColumn<>("Город");

        communityCityColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getCity()
                        )
        );

        TableColumn<Community, Number> communityYearColumn =
                new TableColumn<>("Год");

        communityYearColumn.setCellValueFactory(
                cellData ->
                        new SimpleIntegerProperty(
                                cellData.getValue().getBirthYear()
                        )
        );

        TableColumn<Community, Number> adminColumn =
                new TableColumn<>("ID администратора");

        adminColumn.setCellValueFactory(
                cellData ->
                        new SimpleIntegerProperty(
                                cellData.getValue()
                                        .getAdministratorId()
                        )
        );

        communityTable.getColumns().addAll(
                communityIdColumn,
                communityNameColumn,
                communityCityColumn,
                communityYearColumn,
                adminColumn
        );

        TableColumn<DelProfile, Number> delIdColumn =
                new TableColumn<>("ID");

        delIdColumn.setCellValueFactory(
                cellData ->
                        new SimpleIntegerProperty(
                                cellData.getValue().getId()
                        )
        );

        TableColumn<DelProfile, String> delNameColumn =
                new TableColumn<>("Имя");

        delNameColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getName()
                        )
        );

        TableColumn<DelProfile, String> delCityColumn =
                new TableColumn<>("Город");

        delCityColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getCity()
                        )
        );

        TableColumn<DelProfile, Number> delYearColumn =
                new TableColumn<>("Год рождения");

        delYearColumn.setCellValueFactory(
                cellData ->
                        new SimpleIntegerProperty(
                                cellData.getValue().getBirthYear()
                        )
        );

        TableColumn<DelProfile, String> reasonColumn =
                new TableColumn<>("Причина удаления");

        reasonColumn.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getDelReason()
                        )
        );

        delProfileTable.getColumns().addAll(
                delIdColumn,
                delNameColumn,
                delCityColumn,
                delYearColumn,
                reasonColumn
        );

        typeBox.getItems().addAll(
                "Profile",
                "Community",
                "DelProfile"
        );

        typeBox.setValue("Profile");

        profileTable.setVisible(true);
        profileTable.setManaged(true);

        communityTable.setVisible(false);
        communityTable.setManaged(false);

        delProfileTable.setVisible(false);
        delProfileTable.setManaged(false);

        typeBox.setOnAction(event -> {

            String type =
                    typeBox.getValue();

            boolean isProfile =
                    type.equals("Profile");

            boolean isCommunity =
                    type.equals("Community");

            boolean isDelProfile =
                    type.equals("DelProfile");

            profileTable.setVisible(isProfile);
            profileTable.setManaged(isProfile);

            communityTable.setVisible(isCommunity);
            communityTable.setManaged(isCommunity);

            delProfileTable.setVisible(isDelProfile);
            delProfileTable.setManaged(isDelProfile);

            editButton.setDisable(isDelProfile);
            addButton.setDisable(isDelProfile);
        });

        addButton.setOnAction(event -> {

            String type =
                    typeBox.getValue();

            if (type.equals("Profile")) {

                Dialog<Profile> dialog =
                        new Dialog<>();

                dialog.setTitle("Добавить Profile");
                dialog.setHeaderText(
                        "Введите данные профиля"
                );

                ButtonType addDialogButton =
                        new ButtonType(
                                "Добавить",
                                ButtonBar.ButtonData.OK_DONE
                        );

                ButtonType cancelDialogButton =
                        new ButtonType(
                                "Отмена",
                                ButtonBar.ButtonData.CANCEL_CLOSE
                        );

                dialog.getDialogPane()
                        .getButtonTypes()
                        .addAll(
                                addDialogButton,
                                cancelDialogButton
                        );

                TextField idField =
                        new TextField();

                TextField nameField =
                        new TextField();

                TextField cityField =
                        new TextField();

                TextField yearField =
                        new TextField();

                GridPane grid =
                        createGrid();

                grid.add(
                        new Label("ID:"),
                        0,
                        0
                );

                grid.add(
                        idField,
                        1,
                        0
                );

                grid.add(
                        new Label("Имя:"),
                        0,
                        1
                );

                grid.add(
                        nameField,
                        1,
                        1
                );

                grid.add(
                        new Label("Город:"),
                        0,
                        2
                );

                grid.add(
                        cityField,
                        1,
                        2
                );

                grid.add(
                        new Label("Год рождения:"),
                        0,
                        3
                );

                grid.add(
                        yearField,
                        1,
                        3
                );

                dialog.getDialogPane()
                        .setContent(grid);

                dialog.setResultConverter(button -> {

                    if (button != addDialogButton) {
                        return null;
                    }

                    try {

                        int id =
                                Integer.parseInt(
                                        idField
                                                .getText()
                                                .trim()
                                );

                        String name =
                                nameField
                                        .getText()
                                        .trim();

                        String city =
                                cityField
                                        .getText()
                                        .trim();

                        int year =
                                Integer.parseInt(
                                        yearField
                                                .getText()
                                                .trim()
                                );

                        Profile profile =
                                new Profile(
                                        id,
                                        name,
                                        city,
                                        year
                                );

                        List<String> errors =
                                profile.validate();

                        if (!errors.isEmpty()) {

                            showError(
                                    "Некорректные данные",
                                    String.join(
                                            "\n",
                                            errors
                                    )
                            );

                            return null;
                        }

                        return profile;

                    } catch (NumberFormatException e) {

                        showError(
                                "Ошибка",
                                "ID и год рождения " +
                                        "должны быть числами."
                        );

                        return null;
                    }
                });

                dialog.showAndWait()
                        .ifPresent(profile -> {

                            profileTable
                                    .getItems()
                                    .add(profile);

                            graph.addVertex(
                                    profile.getId()
                            );
                        });

            } else if (type.equals("Community")) {

                Dialog<Community> dialog =
                        new Dialog<>();

                dialog.setTitle(
                        "Добавить Community"
                );

                dialog.setHeaderText(
                        "Введите данные сообщества"
                );

                ButtonType addDialogButton =
                        new ButtonType(
                                "Добавить",
                                ButtonBar.ButtonData.OK_DONE
                        );

                ButtonType cancelDialogButton =
                        new ButtonType(
                                "Отмена",
                                ButtonBar.ButtonData.CANCEL_CLOSE
                        );

                dialog.getDialogPane()
                        .getButtonTypes()
                        .addAll(
                                addDialogButton,
                                cancelDialogButton
                        );

                TextField idField =
                        new TextField();

                TextField nameField =
                        new TextField();

                TextField cityField =
                        new TextField();

                TextField yearField =
                        new TextField();

                TextField adminField =
                        new TextField();

                GridPane grid =
                        createGrid();

                grid.add(
                        new Label("ID:"),
                        0,
                        0
                );

                grid.add(
                        idField,
                        1,
                        0
                );

                grid.add(
                        new Label("Название:"),
                        0,
                        1
                );

                grid.add(
                        nameField,
                        1,
                        1
                );

                grid.add(
                        new Label("Город:"),
                        0,
                        2
                );

                grid.add(
                        cityField,
                        1,
                        2
                );

                grid.add(
                        new Label("Год:"),
                        0,
                        3
                );

                grid.add(
                        yearField,
                        1,
                        3
                );

                grid.add(
                        new Label(
                                "ID администратора:"
                        ),
                        0,
                        4
                );

                grid.add(
                        adminField,
                        1,
                        4
                );

                dialog.getDialogPane()
                        .setContent(grid);

                dialog.setResultConverter(button -> {

                    if (button != addDialogButton) {
                        return null;
                    }

                    try {

                        int id =
                                Integer.parseInt(
                                        idField
                                                .getText()
                                                .trim()
                                );

                        String name =
                                nameField
                                        .getText()
                                        .trim();

                        String city =
                                cityField
                                        .getText()
                                        .trim();

                        int year =
                                Integer.parseInt(
                                        yearField
                                                .getText()
                                                .trim()
                                );

                        int adminId =
                                Integer.parseInt(
                                        adminField
                                                .getText()
                                                .trim()
                                );

                        Community community =
                                new Community(
                                        id,
                                        name,
                                        city,
                                        year,
                                        adminId
                                );

                        List<String> errors =
                                community.validate();

                        if (!errors.isEmpty()) {

                            showError(
                                    "Некорректные данные",
                                    String.join(
                                            "\n",
                                            errors
                                    )
                            );

                            return null;
                        }

                        return community;

                    } catch (NumberFormatException e) {

                        showError(
                                "Ошибка",
                                "ID, год и ID " +
                                        "администратора " +
                                        "должны быть числами."
                        );

                        return null;
                    }
                });

                dialog.showAndWait()
                        .ifPresent(
                                community ->
                                        communityTable
                                                .getItems()
                                                .add(
                                                        community
                                                )
                        );

            } else {

                showInfo(
                        "Недоступно",
                        "DelProfile является " +
                                "только для чтения.\n" +
                                "Добавлять его через GUI нельзя."
                );
            }
        });

        editButton.setOnAction(event -> {

            String type =
                    typeBox.getValue();

            if (type.equals("Profile")) {

                Profile selected =
                        profileTable
                                .getSelectionModel()
                                .getSelectedItem();

                if (selected == null) {

                    showInfo(
                            "Изменение",
                            "Сначала выберите профиль."
                    );

                    return;
                }

                Dialog<Profile> dialog =
                        new Dialog<>();

                dialog.setTitle(
                        "Изменить Profile"
                );

                dialog.setHeaderText(
                        "Изменение профиля"
                );

                ButtonType saveDialogButton =
                        new ButtonType(
                                "Сохранить",
                                ButtonBar.ButtonData.OK_DONE
                        );

                ButtonType cancelDialogButton =
                        new ButtonType(
                                "Отмена",
                                ButtonBar.ButtonData.CANCEL_CLOSE
                        );

                dialog.getDialogPane()
                        .getButtonTypes()
                        .addAll(
                                saveDialogButton,
                                cancelDialogButton
                        );

                TextField idField =
                        new TextField(
                                String.valueOf(
                                        selected.getId()
                                )
                        );

                TextField nameField =
                        new TextField(
                                selected.getName()
                        );

                TextField cityField =
                        new TextField(
                                selected.getCity()
                        );

                TextField yearField =
                        new TextField(
                                String.valueOf(
                                        selected.getBirthYear()
                                )
                        );

                idField.setDisable(true);

                GridPane grid =
                        createGrid();

                grid.add(
                        new Label("ID:"),
                        0,
                        0
                );

                grid.add(
                        idField,
                        1,
                        0
                );

                grid.add(
                        new Label("Имя:"),
                        0,
                        1
                );

                grid.add(
                        nameField,
                        1,
                        1
                );

                grid.add(
                        new Label("Город:"),
                        0,
                        2
                );

                grid.add(
                        cityField,
                        1,
                        2
                );

                grid.add(
                        new Label("Год рождения:"),
                        0,
                        3
                );

                grid.add(
                        yearField,
                        1,
                        3
                );

                dialog.getDialogPane()
                        .setContent(grid);

                dialog.setResultConverter(button -> {

                    if (button != saveDialogButton) {
                        return null;
                    }

                    try {

                        String name =
                                nameField
                                        .getText()
                                        .trim();

                        String city =
                                cityField
                                        .getText()
                                        .trim();

                        int year =
                                Integer.parseInt(
                                        yearField
                                                .getText()
                                                .trim()
                                );

                        Profile testProfile =
                                new Profile(
                                        selected.getId(),
                                        name,
                                        city,
                                        year
                                );

                        List<String> errors =
                                testProfile.validate();

                        if (!errors.isEmpty()) {

                            showError(
                                    "Ошибка",
                                    String.join(
                                            "\n",
                                            errors
                                    )
                            );

                            return null;
                        }

                        selected.setName(name);
                        selected.setCity(city);
                        selected.setBirthYear(year);

                        return selected;

                    } catch (NumberFormatException e) {

                        showError(
                                "Ошибка",
                                "Год рождения " +
                                        "должен быть числом."
                        );

                        return null;
                    }
                });

                dialog.showAndWait()
                        .ifPresent(
                                profile ->
                                        profileTable.refresh()
                        );

            } else if (type.equals("Community")) {

                Community selected =
                        communityTable
                                .getSelectionModel()
                                .getSelectedItem();

                if (selected == null) {

                    showInfo(
                            "Изменение",
                            "Сначала выберите сообщество."
                    );

                    return;
                }

                Dialog<Community> dialog =
                        new Dialog<>();

                dialog.setTitle(
                        "Изменить Community"
                );

                dialog.setHeaderText(
                        "Изменение сообщества"
                );

                ButtonType saveDialogButton =
                        new ButtonType(
                                "Сохранить",
                                ButtonBar.ButtonData.OK_DONE
                        );

                ButtonType cancelDialogButton =
                        new ButtonType(
                                "Отмена",
                                ButtonBar.ButtonData.CANCEL_CLOSE
                        );

                dialog.getDialogPane()
                        .getButtonTypes()
                        .addAll(
                                saveDialogButton,
                                cancelDialogButton
                        );

                TextField idField =
                        new TextField(
                                String.valueOf(
                                        selected.getId()
                                )
                        );

                TextField nameField =
                        new TextField(
                                selected.getName()
                        );

                TextField cityField =
                        new TextField(
                                selected.getCity()
                        );

                TextField yearField =
                        new TextField(
                                String.valueOf(
                                        selected.getBirthYear()
                                )
                        );

                TextField adminField =
                        new TextField(
                                String.valueOf(
                                        selected
                                                .getAdministratorId()
                                )
                        );

                idField.setDisable(true);

                GridPane grid =
                        createGrid();

                grid.add(
                        new Label("ID:"),
                        0,
                        0
                );

                grid.add(
                        idField,
                        1,
                        0
                );

                grid.add(
                        new Label("Название:"),
                        0,
                        1
                );

                grid.add(
                        nameField,
                        1,
                        1
                );

                grid.add(
                        new Label("Город:"),
                        0,
                        2
                );

                grid.add(
                        cityField,
                        1,
                        2
                );

                grid.add(
                        new Label("Год:"),
                        0,
                        3
                );

                grid.add(
                        yearField,
                        1,
                        3
                );

                grid.add(
                        new Label(
                                "ID администратора:"
                        ),
                        0,
                        4
                );

                grid.add(
                        adminField,
                        1,
                        4
                );

                dialog.getDialogPane()
                        .setContent(grid);

                dialog.setResultConverter(button -> {

                    if (button != saveDialogButton) {
                        return null;
                    }

                    try {

                        String name =
                                nameField
                                        .getText()
                                        .trim();

                        String city =
                                cityField
                                        .getText()
                                        .trim();

                        int year =
                                Integer.parseInt(
                                        yearField
                                                .getText()
                                                .trim()
                                );

                        int adminId =
                                Integer.parseInt(
                                        adminField
                                                .getText()
                                                .trim()
                                );

                        Community testCommunity =
                                new Community(
                                        selected.getId(),
                                        name,
                                        city,
                                        year,
                                        adminId
                                );

                        List<String> errors =
                                testCommunity.validate();

                        if (!errors.isEmpty()) {

                            showError(
                                    "Ошибка",
                                    String.join(
                                            "\n",
                                            errors
                                    )
                            );

                            return null;
                        }

                        selected.setName(name);
                        selected.setCity(city);
                        selected.setBirthYear(year);
                        selected.setAdministratorId(
                                adminId
                        );

                        return selected;

                    } catch (NumberFormatException e) {

                        showError(
                                "Ошибка",
                                "Год и ID администратора " +
                                        "должны быть числами."
                        );

                        return null;
                    }
                });

                dialog.showAndWait()
                        .ifPresent(
                                community ->
                                        communityTable.refresh()
                        );

            } else {

                showInfo(
                        "Изменение запрещено",
                        "DelProfile является " +
                                "только для чтения."
                );
            }
        });

        loadButton.setOnAction(event -> {

            String type = typeBox.getValue();

            FileChooser chooser = new FileChooser();

            chooser.setTitle("Выберите CSV файл");

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "CSV files",
                            "*.csv"
                    )
            );

            File file = chooser.showOpenDialog(stage);

            if (file == null) {
                return;
            }

            try {

                if (type.equals("Profile")) {

                    ProfileCsvLoader loader =
                            new ProfileCsvLoader();

                    List<Profile> profiles =
                            loader.load(file.toPath());

                    profileTable.setItems(
                            FXCollections.observableArrayList(
                                    profiles
                            )
                    );

                    for (Profile profile : profiles) {
                        graph.addVertex(profile.getId());
                    }

                    showInfo(
                            "Загрузка",
                            "Загружено профилей: "
                                    + profiles.size()
                    );

                } else if (type.equals("Community")) {

                    CommunityCsvLoader loader =
                            new CommunityCsvLoader();

                    List<Community> communities =
                            loader.load(file.toPath());

                    communityTable.setItems(
                            FXCollections.observableArrayList(
                                    communities
                            )
                    );

                    showInfo(
                            "Загрузка",
                            "Загружено сообществ: "
                                    + communities.size()
                    );

                } else if (type.equals("DelProfile")) {

                    DelProfileCsvLoader loader =
                            new DelProfileCsvLoader();

                    List<DelProfile> deletedProfiles =
                            loader.load(file.toPath());

                    delProfileTable.setItems(
                            FXCollections.observableArrayList(
                                    deletedProfiles
                            )
                    );

                    showInfo(
                            "Загрузка",
                            "Загружено удалённых профилей: "
                                    + deletedProfiles.size()
                    );
                }

            } catch (Exception e) {

                showError(
                        "Ошибка загрузки",
                        e.getMessage()
                );
            }
        });

        saveButton.setOnAction(event -> {

            String type =
                    typeBox.getValue();

            if (!type.equals("Profile")) {

                showInfo(
                        "Сохранение",
                        "Сейчас CSV-сохранение " +
                                "реализовано для Profile."
                );

                return;
            }

            FileChooser chooser =
                    new FileChooser();

            chooser.setTitle(
                    "Сохранить CSV"
            );

            chooser.getExtensionFilters()
                    .add(
                            new FileChooser.ExtensionFilter(
                                    "CSV files",
                                    "*.csv"
                            )
                    );

            chooser.setInitialFileName(
                    "profiles.csv"
            );

            File file =
                    chooser.showSaveDialog(stage);

            if (file == null) {
                return;
            }

            try {

                ProfileCsvSaver saver =
                        new ProfileCsvSaver();

                List<Profile> profiles =
                        new ArrayList<>(
                                profileTable.getItems()
                        );

                saver.save(
                        profiles,
                        file.toPath()
                );

                showInfo(
                        "Сохранение",
                        "Файл успешно сохранён."
                );

            } catch (Exception e) {

                showError(
                        "Ошибка сохранения",
                        e.getMessage()
                );
            }
        });

        bfsButton.setOnAction(event -> {

            Dialog<ButtonType> dialog =
                    new Dialog<>();

            dialog.setTitle("Поиск BFS");

            dialog.setHeaderText(
                    "Кратчайшая цепочка знакомств"
            );

            ButtonType searchButton =
                    new ButtonType(
                            "Найти",
                            ButtonBar.ButtonData.OK_DONE
                    );

            ButtonType cancelButton =
                    new ButtonType(
                            "Отмена",
                            ButtonBar.ButtonData.CANCEL_CLOSE
                    );

            dialog.getDialogPane()
                    .getButtonTypes()
                    .addAll(
                            searchButton,
                            cancelButton
                    );

            TextField startField =
                    new TextField();

            TextField targetField =
                    new TextField();

            GridPane grid =
                    createGrid();

            grid.add(
                    new Label("Начальный ID:"),
                    0,
                    0
            );

            grid.add(
                    startField,
                    1,
                    0
            );

            grid.add(
                    new Label("Конечный ID:"),
                    0,
                    1
            );

            grid.add(
                    targetField,
                    1,
                    1
            );

            dialog.getDialogPane()
                    .setContent(grid);

            dialog.showAndWait()
                    .ifPresent(result -> {

                        if (result != searchButton) {
                            return;
                        }

                        try {

                            int start =
                                    Integer.parseInt(
                                            startField
                                                    .getText()
                                                    .trim()
                                    );

                            int target =
                                    Integer.parseInt(
                                            targetField
                                                    .getText()
                                                    .trim()
                                    );

                            List<Integer> path =
                                    graphAlgorithms.bfs(
                                            graph,
                                            start,
                                            target
                                    );

                            if (path.isEmpty()) {

                                showInfo(
                                        "BFS",
                                        "Путь не найден."
                                );

                            } else {

                                showInfo(
                                        "BFS",
                                        "Путь:\n" +
                                                formatPath(path)
                                );
                            }

                        } catch (
                                NumberFormatException e
                        ) {

                            showError(
                                    "Ошибка",
                                    "ID должны быть числами."
                            );
                        }
                    });
        });

        dijkstraButton.setOnAction(event -> {

            Dialog<ButtonType> dialog =
                    new Dialog<>();

            dialog.setTitle(
                    "Алгоритм Дейкстры"
            );

            dialog.setHeaderText(
                    "Поиск пути с учётом силы дружбы"
            );

            ButtonType searchButton =
                    new ButtonType(
                            "Найти",
                            ButtonBar.ButtonData.OK_DONE
                    );

            ButtonType cancelButton =
                    new ButtonType(
                            "Отмена",
                            ButtonBar.ButtonData.CANCEL_CLOSE
                    );

            dialog.getDialogPane()
                    .getButtonTypes()
                    .addAll(
                            searchButton,
                            cancelButton
                    );

            TextField startField =
                    new TextField();

            TextField targetField =
                    new TextField();

            GridPane grid =
                    createGrid();

            grid.add(
                    new Label("Начальный ID:"),
                    0,
                    0
            );

            grid.add(
                    startField,
                    1,
                    0
            );

            grid.add(
                    new Label("Конечный ID:"),
                    0,
                    1
            );

            grid.add(
                    targetField,
                    1,
                    1
            );

            dialog.getDialogPane()
                    .setContent(grid);

            dialog.showAndWait()
                    .ifPresent(result -> {

                        if (result != searchButton) {
                            return;
                        }

                        try {

                            int start =
                                    Integer.parseInt(
                                            startField
                                                    .getText()
                                                    .trim()
                                    );

                            int target =
                                    Integer.parseInt(
                                            targetField
                                                    .getText()
                                                    .trim()
                                    );

                            List<Integer> path =
                                    graphAlgorithms.deikstra(
                                            graph,
                                            start,
                                            target
                                    );

                            if (path.isEmpty()) {

                                showInfo(
                                        "Dijkstra",
                                        "Путь не найден."
                                );

                            } else {

                                showInfo(
                                        "Dijkstra",
                                        "Путь:\n" +
                                                formatPath(path)
                                );
                            }

                        } catch (
                                NumberFormatException e
                        ) {

                            showError(
                                    "Ошибка",
                                    "ID должны быть числами."
                            );
                        }
                    });
        });

        HBox topPanel =
                new HBox(
                        10,
                        new Label("Тип:"),
                        typeBox,
                        addButton,
                        editButton,
                        loadButton,
                        saveButton,
                        bfsButton,
                        dijkstraButton
                );

        BorderPane root =
                new BorderPane();

        root.setTop(topPanel);

        javafx.scene.layout.StackPane tablePane =
                new javafx.scene.layout.StackPane(
                        profileTable,
                        communityTable,
                        delProfileTable
                );

        root.setCenter(tablePane);

        Scene scene =
                new Scene(
                        root,
                        1000,
                        600
                );

        stage.setTitle(
                "Социальная сеть"
        );

        stage.setScene(scene);

        stage.show();
    }

    private GridPane createGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        return grid;
    }

    private void showError(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void showInfo(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private String formatPath(
            List<Integer> path
    ) {

        return String.join(
                " -> ",
                path.stream()
                        .map(String::valueOf)
                        .toList()
        );
    }

    public static void main(String[] args) {

        launch(args);
    }
}