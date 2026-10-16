
package org.example.social_network.gui;

import javafx.stage.Stage;
import org.example.social_network.model.*;

import java.util.List;

public final class Dialogs {

    private Dialogs() {}

    public static Profile profileDialog(Stage owner, Profile profile) {
        List<FieldSpec> fields = List.of(
                new FieldSpec("ID:",
                        profile != null ? String.valueOf(profile.getId()) : ""),
                new FieldSpec("Имя:",
                        profile != null ? profile.getName() : ""),
                new FieldSpec("Город:",
                        profile != null ? profile.getCity() : ""),
                new FieldSpec("Год рождения:",
                        profile != null ? String.valueOf(profile.getBirthYear()) : "")
        );

        return GenericDialog.show(
                owner,
                profile == null ? "Добавить профиль" : "Изменить профиль",
                fields,
                values -> new Profile(
                        Integer.parseInt(values.get(0)),
                        values.get(1),
                        values.get(2),
                        Integer.parseInt(values.get(3))
                )
        );
    }

    public static Community communityDialog(Stage owner, Community community) {
        List<FieldSpec> fields = List.of(
                new FieldSpec("ID:",
                        community != null ? String.valueOf(community.getId()) : "",
                        community != null),
                new FieldSpec("Название:",
                        community != null ? community.getName() : ""),
                new FieldSpec("Город:",
                        community != null ? community.getCity() : ""),
                new FieldSpec("Год создания:",
                        community != null ? String.valueOf(community.getBirthYear()) : ""),
                new FieldSpec("Администратор:",
                        community != null ? String.valueOf(community.getAdminId()) : "")
        );

        return GenericDialog.show(
                owner,
                community == null ? "Добавить сообщество" : "Изменить сообщество",
                fields,
                values -> new Community(
                        Integer.parseInt(values.get(0)),
                        values.get(1),
                        values.get(2),
                        Integer.parseInt(values.get(3)),
                        Integer.parseInt(values.get(4))
                )
        );
    }

    public static FriendShip friendshipDialog(
            Stage owner,
            FriendShip friendship
    ) {
        List<FieldSpec> fields = List.of(
                new FieldSpec("Профиль 1:",
                        friendship != null
                                ? String.valueOf(friendship.getProfile1()) : "",
                        friendship != null),
                new FieldSpec("Профиль 2:",
                        friendship != null
                                ? String.valueOf(friendship.getProfile2()) : "",
                        friendship != null),
                new FieldSpec("Сила связи:",
                        friendship != null
                                ? String.valueOf(friendship.getStrength()) : "")
        );

        return GenericDialog.show(
                owner,
                friendship == null ? "Добавить дружбу" : "Изменить дружбу",
                fields,
                values -> new FriendShip(
                        Integer.parseInt(values.get(0)),
                        Integer.parseInt(values.get(1)),
                        Integer.parseInt(values.get(2))
                )
        );
    }
}
