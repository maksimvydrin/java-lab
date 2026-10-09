package org.example.social_network.gui;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.social_network.model.*;

public class TableManager {
    public static void updateColumnsAndData(
            TableView<Object> table,
            MainApp.Type type,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<DelProfile> deleted,
            ObservableList<FriendShip> friendships) {

        table.getColumns().clear();
        switch (type) {
            case PROFILE -> {
                table.getColumns().addAll(
                        createColumn("ID", "id"),
                        createColumn("Имя", "name"),
                        createColumn("Город", "city"),
                        createColumn("Год рождения", "birthYear")
                );
                table.setItems((ObservableList) profiles);
            }
            case COMMUNITY -> {
                table.getColumns().addAll(
                        createColumn("ID", "id"),
                        createColumn("Название", "name"),
                        createColumn("Город", "city"),
                        createColumn("Год основания", "birthYear"),
                        createColumn("ID Админа", "adminId")
                );
                table.setItems((ObservableList) communities);
            }
            case DELETED -> {
                table.getColumns().addAll(
                        createColumn("ID", "id"),
                        createColumn("Имя", "name"),
                        createColumn("Город", "city"),
                        createColumn("Год рождения", "birthYear"),
                        createColumn("Дата удаления", "dayDel"),
                        createColumn("Причина", "delReason")
                );
                table.setItems((ObservableList) deleted);
            }
            case FRIENDSHIP -> {
                table.getColumns().addAll(
                        createColumn("Профиль 1", "profile1"),
                        createColumn("Профиль 2", "profile2"),
                        createColumn("Сила связи", "strength")
                );
                table.setItems((ObservableList) friendships);
            }
        }
    }
    private static <S, T> TableColumn<S, T> createColumn(String title, String propertyName) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return col;
    }
}