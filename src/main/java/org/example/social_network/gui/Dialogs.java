package org.example.social_network.gui;

import javafx.stage.Stage;
import org.example.social_network.model.*;

import java.util.List;

public final class Dialogs {

    private Dialogs() {}

    public static Profile profileDialog(Stage owner, Profile profile) {
        boolean isEdit = (profile != null);
        String title = isEdit ? "Изменить профиль" : "Добавить профиль";

        List<FieldSpec> fields = List.of(
                new FieldSpec("ID:", isEdit ? String.valueOf(profile.getId()) : ""),
                new FieldSpec("Имя:", isEdit ? profile.getName() : ""),
                new FieldSpec("Город:", isEdit ? profile.getCity() : ""),
                new FieldSpec("Год рождения:", isEdit ? String.valueOf(profile.getBirthYear()) : "")
        );

        return GenericDialog.show(owner, title, fields, values -> {
            int id = Integer.parseInt(values.get(0));
            String name = values.get(1);
            String city = values.get(2);
            int birthYear = Integer.parseInt(values.get(3));

            return new Profile(id, name, city, birthYear);
        });
    }

    public static Community communityDialog(Stage owner, Community community) {
        boolean isEdit = (community != null);
        String title = isEdit ? "Изменить сообщество" : "Добавить сообщество";

        List<FieldSpec> fields = List.of(
                new FieldSpec("ID:", isEdit ? String.valueOf(community.getId()) : "", isEdit),
                new FieldSpec("Название:", isEdit ? community.getName() : ""),
                new FieldSpec("Город:", isEdit ? community.getCity() : ""),
                new FieldSpec("Год создания:", isEdit ? String.valueOf(community.getBirthYear()) : ""),
                new FieldSpec("Администратор:", isEdit ? String.valueOf(community.getAdminId()) : "")
        );

        return GenericDialog.show(owner, title, fields, values -> {
            int id = Integer.parseInt(values.get(0));
            String name = values.get(1);
            String city = values.get(2);
            int birthYear = Integer.parseInt(values.get(3));
            int adminId = Integer.parseInt(values.get(4));

            return new Community(id, name, city, birthYear, adminId);
        });
    }

    public static FriendShip friendshipDialog(Stage owner, FriendShip friendship) {
        boolean isEdit = (friendship != null);
        String title = isEdit ? "Изменить дружбу" : "Добавить дружбу";

        List<FieldSpec> fields = List.of(
                new FieldSpec("Профиль 1:", isEdit ? String.valueOf(friendship.getProfile1()) : "", isEdit),
                new FieldSpec("Профиль 2:", isEdit ? String.valueOf(friendship.getProfile2()) : "", isEdit),
                new FieldSpec("Сила связи:", isEdit ? String.valueOf(friendship.getStrength()) : "")
        );

        return GenericDialog.show(owner, title, fields, values -> {
            int profile1 = Integer.parseInt(values.get(0));
            int profile2 = Integer.parseInt(values.get(1));
            int strength = Integer.parseInt(values.get(2));

            return new FriendShip(profile1, profile2, strength);
        });
    }
}