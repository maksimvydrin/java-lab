
package org.example.social_network.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import org.example.social_network.controller.CsvController;
import org.example.social_network.controller.Controller;
import org.example.social_network.controller.DataGenerator;
import org.example.social_network.graph.SocialGraph;
import org.example.social_network.model.*;
import org.example.social_network.graph.GraphAlgorithms;
import java.util.List;

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

    private final Controller entityController = new Controller();
    private final CsvController csvController = new CsvController();
    private final SocialGraph<Integer, Profile> socialGraph = new SocialGraph<>();

    private GraphCanvas graphCanvas;

    @Override
    public void start(Stage stage) {
        configureTypeBox();

        Button addButton = new Button("Добавить");
        Button loadButton = new Button("Загрузить CSV");
        Button saveButton = new Button("Сохранить CSV");

        configureActions(stage, addButton, loadButton, saveButton);
        configureTableSelection();

        TextField genCountField = new TextField("1000");
        genCountField.setPrefWidth(70);
        Button genButton = new Button("Сгенерировать");
        Button cancelGenButton = new Button("Отмена");
        Button pathButton = new Button("Найти путь");
        pathButton.setOnAction(event -> findPathDialog());
        cancelGenButton.setDisable(true);

        DataGenerator dataGenerator = new DataGenerator();

        genButton.setOnAction(event -> {
            try {
                int count = Integer.parseInt(genCountField.getText());
                genButton.setDisable(true);
                cancelGenButton.setDisable(false);


                dataGenerator.startGeneration(profiles, friendships, count);

            } catch (NumberFormatException e) {
                GenericDialog.showAlert("Введите целое число для генерации");
            }
        });

        cancelGenButton.setOnAction(event -> {
            dataGenerator.cancelGeneration();
            genButton.setDisable(false);
            cancelGenButton.setDisable(true);
        });

        HBox controls = new HBox(10,
                typeBox,
                addButton,
                editButton,
                loadButton,
                saveButton,
                pathButton,
                genCountField,
                genButton,
                cancelGenButton);

        graphCanvas = new GraphCanvas(profiles, friendships);

        SplitPane content = new SplitPane(table, graphCanvas);
        content.setDividerPositions(0.48);

        VBox root = new VBox(10, controls, content);
        root.setPadding(new Insets(10));
        VBox.setVgrow(content, javafx.scene.layout.Priority.ALWAYS);

        refreshTable();

        stage.setTitle("Социальная сеть");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    private void findPathDialog() {
        rebuildGraph();

        if (socialGraph.size() == 0) {
            GenericDialog.showAlert("Граф пуст. Сначала добавьте или сгенерируйте профили.");
            return;
        }

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Найти кратчайший путь");
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField startField = new TextField();
        TextField targetField = new TextField();

        grid.add(new Label("ID откуда:"), 0, 0);
        grid.add(startField, 1, 0);
        grid.add(new Label("ID куда:"), 0, 1);
        grid.add(targetField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new String[]{startField.getText(), targetField.getText()};
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                int startId = Integer.parseInt(result[0].trim());
                int targetId = Integer.parseInt(result[1].trim());

                org.example.social_network.graph.GraphAlgorithms algo =
                        new org.example.social_network.graph.GraphAlgorithms();

                java.util.List<Integer> path = algo.dijkstra(socialGraph, startId, targetId);

                if (path.isEmpty()) {
                    GenericDialog.showAlert("Путь не найден или вершины отсутствуют.");
                } else {
                    GenericDialog.showAlert("Кратчайший путь: " + path.toString());
                }
            } catch (NumberFormatException e) {
                GenericDialog.showAlert("Введите корректные числовые ID.");
            }
        });
    }

    private void configureTypeBox() {
        typeBox.getItems().addAll(Type.values());
        typeBox.setValue(Type.PROFILE);
        typeBox.setOnAction(event -> refreshTable());
    }

    private void configureActions(
            Stage stage,
            Button addButton,
            Button loadButton,
            Button saveButton
    ) {
        addButton.setOnAction(
                event -> addEntity(stage)
        );

        editButton.setOnAction(
                event -> editEntity(stage)
        );

        loadButton.setOnAction(
                event -> loadCsv(stage)
        );

        saveButton.setOnAction(
                event -> saveCsv(stage)
        );
    }

    private void configureTableSelection() {
        table.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        updateEditButton()
                );
    }

    private void addEntity(Stage stage) {
        entityController.add(
                stage,
                typeBox.getValue(),
                profiles,
                communities,
                friendships
        );

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
        csvController.load(
                stage,
                typeBox.getValue(),
                profiles,
                communities,
                deleted,
                friendships
        );

        refreshTable();
    }

    private void saveCsv(Stage stage) {
        csvController.save(
                stage,
                typeBox.getValue(),
                profiles,
                communities,
                deleted,
                friendships
        );
    }

    private void rebuildGraph() {
        socialGraph.clear();

        for (Profile profile : profiles) {
            if (!socialGraph.containsVertex(profile.getId())) {
                socialGraph.addVertex(profile.getId(), profile);
            }
        }

        for (FriendShip friendship : friendships) {
            if (friendship.validate().isEmpty()
                    && socialGraph.containsVertex(friendship.getProfile1())
                    && socialGraph.containsVertex(friendship.getProfile2())) {

                socialGraph.addEdge(
                        friendship.getProfile1(),
                        friendship.getProfile2(),
                        friendship.getStrength()
                );
            }
        }
    }

    private void refreshTable() {
        TableManager.updateColumnsAndData(
                table,
                typeBox.getValue(),
                profiles,
                communities,
                deleted,
                friendships
        );

        rebuildGraph();
        updateEditButton();

        if (graphCanvas != null) {
            graphCanvas.refresh();
        }
    }

    public SocialGraph getSocialGraph() {
        return socialGraph;
    }

    private void updateEditButton() {
        Object selected = table.getSelectionModel().getSelectedItem();
        editButton.setDisable(!(selected instanceof Editable));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
