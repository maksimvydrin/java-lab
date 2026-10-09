package org.example.social_network.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.social_network.model.Editable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class GenericEntityDialog {

    private GenericEntityDialog() {}
    public static <T extends Editable> T show(
            Stage owner,
            String title,
            List<FieldSpec> fields,
            Function<List<String>, T> mapper)
    {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(title);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        List<TextField> textFields = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            FieldSpec spec = fields.get(i);
            grid.add(new Label(spec.label()), 0, i);

            TextField tf = new TextField(spec.initValue() != null ? spec.initValue() : "");
            tf.setDisable(spec.disabled());
            textFields.add(tf);

            grid.add(tf, 1, i);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return null;
        }

        try {
            List<String> values = textFields.stream().map(TextField::getText).toList();

            T result = mapper.apply(values);

            if (result != null) {
                List<String> errors = result.validate();
                if (!errors.isEmpty()) {
                    showAlert(String.join("\n", errors));
                    return null;
                }
            }

            return result;
        } catch (NumberFormatException e) {
            showAlert("Поля с числами должны содержать корректные числовые значения.");
            return null;
        } catch (Exception e) {
            showAlert("Ошибка ввода данных: " + e.getMessage());
            return null;
        }
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Социальная сеть");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}