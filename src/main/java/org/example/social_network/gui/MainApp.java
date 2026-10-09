package org.example.social_network.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.example.social_network.controller.CsvController;
import org.example.social_network.controller.EntityController;
import org.example.social_network.model.*;

public class MainApp extends javafx.application.Application {

    public enum Type {
        PROFILE("Профили"),
        COMMUNITY("Сообщества"),
        DELETED("Удалённые профили"),
        FRIENDSHIP("Дружба");

        private final String title;

        Type(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final ObservableList<Profile> profiles = FXCollections.observableArrayList();
    private final ObservableList<Community> communities = FXCollections.observableArrayList();
    private final ObservableList<DelProfile> deleted = FXCollections.observableArrayList();
    private final ObservableList<FriendShip> friendships = FXCollections.observableArrayList();

    private final TableView<Object> table = new TableView<>();
    private final ComboBox<Type> typeBox = new ComboBox<>();
    private final Button editButton = new Button("Изменить");

    private final EntityController entityController = new EntityController();
    private final CsvController csvController = new CsvController();

    @Override
    public void start(Stage stage) {

        configureTypeBox();

        Button addButton = new Button("Добавить");
        Button loadButton = new Button("Загрузить CSV");
        Button saveButton = new Button("Сохранить CSV");

        configureActions(stage, addButton, loadButton, saveButton);
        configureTableSelection();
        HBox controls = new HBox(10, typeBox, addButton, editButton, loadButton, saveButton);

        VBox root = new VBox(10, controls, table);
        root.setPadding(new Insets(10));

        refreshTable();

        stage.setTitle("Социальная сеть");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    private void configureTypeBox() {
        typeBox.getItems().addAll(Type.values());
        typeBox.setValue(Type.PROFILE);
        typeBox.setOnAction(event -> refreshTable());
    }

    private void configureActions(Stage stage, Button addButton, Button loadButton, Button saveButton) {
        addButton.setOnAction(event -> addEntity(stage));
        editButton.setOnAction(event -> editEntity(stage));
        loadButton.setOnAction(event -> loadCsv(stage));
        saveButton.setOnAction(event -> saveCsv(stage));
    }

    private void configureTableSelection() {
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateEditButton());
    }

    private void addEntity(Stage stage) {
        entityController.add(stage, typeBox.getValue(), profiles, communities, friendships);
        refreshTable();
    }

    private void editEntity(Stage stage) {
        Object selected = table.getSelectionModel().getSelectedItem();

        if (!(selected instanceof Editable)) {
            return;
        }

        entityController.edit(stage, selected);
        refreshTable();
    }

    private void loadCsv(Stage stage) {
        csvController.load(stage, typeBox.getValue(), profiles, communities, deleted, friendships);
        refreshTable();
    }

    private void saveCsv(Stage stage) {
        csvController.save(stage, typeBox.getValue(), profiles, communities, deleted, friendships);
    }

    private void refreshTable() {
        TableManager.updateColumnsAndData(table, typeBox.getValue(), profiles, communities, deleted, friendships);
        updateEditButton();
    }

    private void updateEditButton() {
        Object selected = table.getSelectionModel().getSelectedItem();
        editButton.setDisable(!(selected instanceof Editable));
    }

    public static void main(String[] args) {
        launch(args);
    }
}