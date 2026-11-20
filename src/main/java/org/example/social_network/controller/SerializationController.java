package org.example.social_network.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.social_network.gui.GenericDialog;
import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;
import org.example.social_network.serialization.BinSocialSerializer;
import org.example.social_network.serialization.JsonSocialSerializer;
import org.example.social_network.serialization.Serializer;
import org.example.social_network.serialization.SocialNetworkData;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

public final class SerializationController {

    private final Serializer<SocialNetworkData>[] serializers = new Serializer[] {
            new JsonSocialSerializer(),
            new BinSocialSerializer()
    };

    public void exportData(Stage stage, ObservableList<Profile> profiles, ObservableList<Community> communities,
                           ObservableList<DelProfile> deleted, ObservableList<FriendShip> friendships) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Выбор версии экспорта");
        alert.setHeaderText("В какой версии сохранить файл?");
        alert.setContentText(
                "v3 - Текущая (рейтинг + заметка)\n" +
                        "v2 - Старая (заметка будет утеряна)\n" +
                        "v1 - Самая старая (рейтинг и заметка будут утеряны)"
        );

        ButtonType btnV3 = new ButtonType("v3 (Текущая)");
        ButtonType btnV2 = new ButtonType("v2");
        ButtonType btnV1 = new ButtonType("v1");
        ButtonType btnCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnV3, btnV2, btnV1, btnCancel);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isEmpty() || res.get() == btnCancel) return;

        int targetVersion = 3;
        if (res.get() == btnV2) {
            targetVersion = 2;
            GenericDialog.showAlert("Внимание! При экспорте в v2 поле 'заметка' будет потеряно.");
        } else if (res.get() == btnV1) {
            targetVersion = 1;
            GenericDialog.showAlert("Внимание! При экспорте в v1 поля 'рейтинг' и 'заметка' будут потеряны.");
        }

        File file = chooseFile(stage, "Экспорт данных (v" + targetVersion + ")", true);
        if (file == null) return;

        Serializer<SocialNetworkData> serializer = resolveSerializer(file);

        SocialNetworkData data = new SocialNetworkData(
                profiles.toArray(new Profile[0]),
                communities.toArray(new Community[0]),
                deleted.toArray(new DelProfile[0]),
                friendships.toArray(new FriendShip[0])
        );

        try {
            serializer.save(data, file.toPath(), targetVersion);
            GenericDialog.showAlert("Экспортировано (" + serializer.format().toUpperCase() + ", v" + targetVersion + ").");
        } catch (IOException e) {
            GenericDialog.showAlert("Ошибка экспорта: " + e.getMessage());
        }
    }

    public void importData(Stage stage, ObservableList<Profile> profiles, ObservableList<Community> communities,
                           ObservableList<DelProfile> deleted, ObservableList<FriendShip> friendships, Runnable onLoadedCallback) {

        File file = chooseFile(stage, "Импорт данных", false);
        if (file == null) return;

        Serializer<SocialNetworkData> serializer = resolveSerializer(file);

        // Функция-коллбэк для запроса у пользователя согласия на миграцию (запускается в JavaFX-потоке)
        BiFunction<Integer, Integer, Boolean> askMigration = (from, to) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Миграция данных");
            alert.setHeaderText("Файл версии " + from + ". Мигрировать до " + to + "?");
            return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
        };

        try {
            SocialNetworkData data = serializer.load(file.toPath(), askMigration);

            profiles.setAll(data.getProfiles());
            communities.setAll(data.getCommunities());
            deleted.setAll(data.getDeletedProfiles());
            friendships.setAll(data.getFriendships());

            if (onLoadedCallback != null) onLoadedCallback.run();
            GenericDialog.showAlert("Успешно импортировано (" + serializer.format().toUpperCase() + ").");
        } catch (IOException e) {
            GenericDialog.showAlert("Ошибка импорта:\n" + e.getMessage());
        }
    }

    private Serializer<SocialNetworkData> resolveSerializer(File file) {
        String name = file.getName().toLowerCase();
        for (Serializer<SocialNetworkData> s : serializers) {
            if (name.endsWith("." + s.format())) return s;
        }
        return serializers[0];
    }

    private File chooseFile(Stage stage, String title, boolean save) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON файлы (*.json)", "*.json"),
                new FileChooser.ExtensionFilter("Бинарные файлы (*.bin)", "*.bin")
        );
        return save ? fc.showSaveDialog(stage) : fc.showOpenDialog(stage);
    }
}