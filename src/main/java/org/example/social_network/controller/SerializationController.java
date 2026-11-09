package org.example.social_network.controller;

import javafx.collections.ObservableList;
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

public final class SerializationController {

    private final Serializer<SocialNetworkData>[] serializers = new Serializer[] {
            new JsonSocialSerializer(),
            new BinSocialSerializer()
    };

    public void exportData(
            Stage stage,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<DelProfile> deleted,
            ObservableList<FriendShip> friendships
    ) {
        File file = chooseFile(stage, "Экспорт данных", true);
        if (file == null) return;

        Serializer<SocialNetworkData> serializer = resolveSerializer(file);

        // Превращаем ObservableList от UI напрямую в массивы
        SocialNetworkData data = new SocialNetworkData(
                profiles.toArray(new Profile[0]),
                communities.toArray(new Community[0]),
                deleted.toArray(new DelProfile[0]),
                friendships.toArray(new FriendShip[0])
        );

        try {
            serializer.save(data, file.toPath());
            GenericDialog.showAlert("Данные успешно экспортированы (" + serializer.format().toUpperCase() + ").");
        } catch (IOException e) {
            GenericDialog.showAlert("Ошибка экспорта: " + e.getMessage());
        }
    }

    public void importData(
            Stage stage,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<DelProfile> deleted,
            ObservableList<FriendShip> friendships,
            Runnable onLoadedCallback
    ) {
        File file = chooseFile(stage, "Импорт данных", false);
        if (file == null) return;

        Serializer<SocialNetworkData> serializer = resolveSerializer(file);

        try {
            SocialNetworkData data = serializer.load(file.toPath());

            // Метод setAll отлично принимает сырые массивы (varargs)
            profiles.setAll(data.getProfiles());
            communities.setAll(data.getCommunities());
            deleted.setAll(data.getDeletedProfiles());
            friendships.setAll(data.getFriendships());

            if (onLoadedCallback != null) {
                onLoadedCallback.run();
            }
            GenericDialog.showAlert("Данные успешно импортированы (" + serializer.format().toUpperCase() + ").");
        } catch (IOException e) {
            GenericDialog.showAlert("Ошибка импорта (файл повреждён или неверный формат):\n" + e.getMessage());
        }
    }

    private Serializer<SocialNetworkData> resolveSerializer(File file) {
        String name = file.getName().toLowerCase();
        for (Serializer<SocialNetworkData> s : serializers) {
            if (name.endsWith("." + s.format())) {
                return s;
            }
        }
        return serializers[0]; // По умолчанию JSON
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