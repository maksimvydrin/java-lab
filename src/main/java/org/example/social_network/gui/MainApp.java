package org.example.social_network.gui;

import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.social_network.csv.ProfileCsvLoader;
import org.example.social_network.csv.ProfileCsvSaver;

import org.example.social_network.model.Profile;

import java.util.List;

public class MainApp extends Application {

    private final TableView<Profile> table = new TableView<>();

    @Override
    public void start(Stage stage) {
        // Колонка ID
        TableColumn<Profile, Number> idColumn =
                new TableColumn<>("ID");

        idColumn.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(
                        cellData.getValue().getId()
                )
        );
        // Колонка имени

        TableColumn<Profile, String> nameColumn =
                new TableColumn<>("Имя");

        nameColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getName()
                )
        );

        // Колонка города


        TableColumn<Profile, String> cityColumn =
                new TableColumn<>("Город");

        cityColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getCity()
                )
        );

        // Колонка года рождения

        TableColumn<Profile, Number> yearColumn =
                new TableColumn<>("Год рождения");

        yearColumn.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(
                        cellData.getValue().getBirthYear()
                )
        );

        table.getColumns().addAll(
                idColumn,
                nameColumn,
                cityColumn,
                yearColumn
        );


        // Тестовые данные


        Profile profile1 =
                new Profile(1, "Иван", "Москва", 2000);

        Profile profile2 =
                new Profile(2, "Пётр", "Казань", 1999);

        Profile profile3 =
                new Profile(3, "Анна", "Самара", 2001);

        table.setItems(
                FXCollections.observableArrayList(
                        profile1,
                        profile2,
                        profile3
                )
        );


        // Выбор типа


        ComboBox<String> typeBox =
                new ComboBox<>();

        typeBox.getItems().addAll(
                "Profile",
                "Community",
                "DelProfile"
        );

        typeBox.setValue("Profile");

        // Кнопки

        Button addButton =
                new Button("Добавить");

        Button editButton =
                new Button("Изменить");

        Button loadButton =
                new Button("Загрузить");
        loadButton.setOnAction(event -> {

            FileChooser fileChooser =
                    new FileChooser();

            fileChooser.setTitle(
                    "Выберите файл с профилями"
            );

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "CSV файлы",
                            "*.csv"
                    )
            );

            Stage currentStage =
                    (Stage) loadButton.getScene().getWindow();

            File file =
                    fileChooser.showOpenDialog(
                            currentStage
                    );

            if (file == null) {
                return;
            }

            try {

                ProfileCsvLoader loader =
                        new ProfileCsvLoader();

                List<Profile> loadedProfiles =
                        loader.load(file.toPath());

                table.setItems(
                        FXCollections.observableArrayList(
                                loadedProfiles
                        )
                );

                Alert alert =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                alert.setTitle("Загрузка");
                alert.setHeaderText(null);

                alert.setContentText(
                        "Загружено профилей: "
                                + loadedProfiles.size()
                );

                alert.showAndWait();

            } catch (Exception e) {

                Alert alert =
                        new Alert(
                                Alert.AlertType.ERROR
                        );

                alert.setTitle("Ошибка загрузки");
                alert.setHeaderText(
                        "Не удалось загрузить файл"
                );

                alert.setContentText(
                        e.getMessage()
                );

                alert.showAndWait();
            }
        });

        Button saveButton =
                new Button("Сохранить");
        saveButton.setOnAction(event -> {

            FileChooser fileChooser =
                    new FileChooser();

            fileChooser.setTitle(
                    "Сохранить профили"
            );

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "CSV файлы",
                            "*.csv"
                    )
            );

            fileChooser.setInitialFileName(
                    "profiles.csv"
            );

            Stage currentStage =
                    (Stage) saveButton.getScene().getWindow();

            File file =
                    fileChooser.showSaveDialog(
                            currentStage
                    );

            if (file == null) {
                return;
            }

            try {

                ProfileCsvSaver saver =
                        new ProfileCsvSaver();

                List<Profile> profiles =
                        new ArrayList<>(
                                table.getItems()
                        );

                saver.save(
                        profiles,
                        file.toPath()
                );

                Alert alert =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                alert.setTitle("Сохранение");
                alert.setHeaderText(null);

                alert.setContentText(
                        "Профили сохранены:\n"
                                + file.getAbsolutePath()
                );

                alert.showAndWait();

            } catch (Exception e) {

                Alert alert =
                        new Alert(
                                Alert.AlertType.ERROR
                        );

                alert.setTitle("Ошибка сохранения");
                alert.setHeaderText(
                        "Не удалось сохранить файл"
                );

                alert.setContentText(
                        e.getMessage()
                );

                alert.showAndWait();
            }
        });

        Button bfsButton =
                new Button("BFS");

        Button dijkstraButton =
                new Button("Dijkstra");


        // Кнопка Добавить


        addButton.setOnAction(event -> {

            // Сейчас добавляем только Profile.
            // Community сделаем следующим шагом.

            if (!typeBox.getValue().equals("Profile")) {

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION
                );

                alert.setTitle("Добавление");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Сейчас можно добавлять только Profile."
                );

                alert.showAndWait();

                return;
            }

            Dialog<Profile> dialog =
                    new Dialog<>();

            dialog.setTitle("Добавить Profile");
            dialog.setHeaderText("Введите данные профиля");

            ButtonType okButton =
                    new ButtonType(
                            "Добавить",
                            ButtonBar.ButtonData.OK_DONE
                    );

            ButtonType cancelButton =
                    new ButtonType(
                            "Отмена",
                            ButtonBar.ButtonData.CANCEL_CLOSE
                    );

            dialog.getDialogPane().getButtonTypes().addAll(
                    okButton,
                    cancelButton
            );


            // Поля формы


            TextField idField =
                    new TextField();

            TextField nameField =
                    new TextField();

            TextField cityField =
                    new TextField();

            TextField yearField =
                    new TextField();

            idField.setPromptText("Например: 4");
            nameField.setPromptText("Например: Сергей");
            cityField.setPromptText("Например: Москва");
            yearField.setPromptText("Например: 2000");

            GridPane grid =
                    new GridPane();

            grid.setHgap(10);
            grid.setVgap(10);

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


            // Что происходит при OK


            dialog.setResultConverter(button -> {

                if (button != okButton) {
                    return null;
                }

                try {

                    int id =
                            Integer.parseInt(
                                    idField.getText()
                            );

                    String name =
                            nameField.getText();

                    String city =
                            cityField.getText();

                    int birthYear =
                            Integer.parseInt(
                                    yearField.getText()
                            );

                    Profile profile =
                            new Profile(
                                    id,
                                    name,
                                    city,
                                    birthYear
                            );

                    // Проверяем объект
                    List<String> errors =
                            profile.validate();

                    if (!errors.isEmpty()) {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle("Ошибка");
                        alert.setHeaderText(
                                "Некорректные данные"
                        );

                        alert.setContentText(
                                String.join(
                                        "\n",
                                        errors
                                )
                        );

                        alert.showAndWait();

                        return null;
                    }

                    return profile;

                } catch (NumberFormatException e) {

                    Alert alert =
                            new Alert(
                                    Alert.AlertType.ERROR
                            );

                    alert.setTitle("Ошибка");
                    alert.setHeaderText(
                            "Неверное число"
                    );

                    alert.setContentText(
                            "ID и год рождения должны быть числами."
                    );

                    alert.showAndWait();

                    return null;
                }
            });

            dialog.showAndWait().ifPresent(
                    profile -> {

                        table.getItems().add(
                                profile
                        );

                        table.refresh();
                    }
            );
        });


// Кнопка Изменить

        editButton.setOnAction(event -> {

            // Изменять можно только Profile
            if (!typeBox.getValue().equals("Profile")) {

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION
                );

                alert.setTitle("Изменение");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Сейчас можно изменять только Profile."
                );

                alert.showAndWait();

                return;
            }

            // Получаем выбранную строку
            Profile selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {

                Alert alert = new Alert(
                        Alert.AlertType.WARNING
                );

                alert.setTitle("Изменение");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Сначала выберите профиль в таблице."
                );

                alert.showAndWait();

                return;
            }

            // Создаём окно
            Dialog<Profile> dialog =
                    new Dialog<>();

            dialog.setTitle("Изменить Profile");
            dialog.setHeaderText(
                    "Изменение выбранного профиля"
            );

            ButtonType dialogSaveButton =
                    new ButtonType(
                            "Сохранить",
                            ButtonBar.ButtonData.OK_DONE
                    );

            ButtonType cancelButton =
                    new ButtonType(
                            "Отмена",
                            ButtonBar.ButtonData.CANCEL_CLOSE
                    );

            dialog.getDialogPane().getButtonTypes().addAll(
                    dialogSaveButton,
                    cancelButton
            );

            // Поля


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

            // ID лучше не изменять
            idField.setDisable(true);

            GridPane grid =
                    new GridPane();

            grid.setHgap(10);
            grid.setVgap(10);

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


            // Сохранение изменений


            dialog.setResultConverter(button -> {

                if (button != dialogSaveButton) {
                    return null;
                }

                try {

                    String name =
                            nameField.getText();

                    String city =
                            cityField.getText();

                    int birthYear =
                            Integer.parseInt(
                                    yearField.getText()
                            );

                    // Меняем существующий объект
                    selected.setName(name);
                    selected.setCity(city);
                    selected.setBirthYear(birthYear);

                    // Проверяем
                    List<String> errors =
                            selected.validate();

                    if (!errors.isEmpty()) {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle("Ошибка");
                        alert.setHeaderText(
                                "Некорректные данные"
                        );

                        alert.setContentText(
                                String.join(
                                        "\n",
                                        errors
                                )
                        );

                        alert.showAndWait();

                        return null;
                    }

                    return selected;

                } catch (NumberFormatException e) {

                    Alert alert =
                            new Alert(
                                    Alert.AlertType.ERROR
                            );

                    alert.setTitle("Ошибка");

                    alert.setHeaderText(
                            "Неверное число"
                    );

                    alert.setContentText(
                            "Год рождения должен быть числом."
                    );

                    alert.showAndWait();

                    return null;
                }
            });

            dialog.showAndWait().ifPresent(
                    profile -> table.refresh()
            );
        });


        // Верхняя панель


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


        // Главное окно


        BorderPane root =
                new BorderPane();

        root.setTop(topPanel);
        root.setCenter(table);

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

    public static void main(String[] args) {
        launch(args);
    }
}