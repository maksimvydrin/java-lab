package org.example.social_network.gui;

import javafx.stage.Stage;
import org.example.social_network.model.*;

import java.util.List;

public final class EntityDialogs {
    private EntityDialogs() {}
    public static Profile profileDialog(Stage owner, Profile profile) {
        List<FieldSpec> fields = List.of(
                new FieldSpec("ID:", profile != null ? String.valueOf(profile.getId()) : ""),
                new FieldSpec("Имя:", profile != null ? profile.getName() : ""),
                new FieldSpec("Город:", profile != null ? profile.getCity() : ""),
                new FieldSpec("Год рождения:", profile != null ? String.valueOf(profile.getBirthYear()) : "")
        );
        return GenericEntityDialog.show(
                owner,
                profile == null ? "Добавить профиль" : "Изменить профиль",
                fields,
                values -> {
                    int id = Integer.parseInt(values.get(0));
                    String name = values.get(1);
                    String city = values.get(2);
                    int birthYear = Integer.parseInt(values.get(3));

                    if (profile == null) {
                        return new Profile(id, name, city, birthYear);
                    } else {
                        profile.setId(id);
                        profile.setName(name);
                        profile.setCity(city);
                        profile.setBirthYear(birthYear);
                        return profile;
                    }
                }
        );
    }

    public static Community communityDialog(Stage owner, Community community) {
        List<FieldSpec> fields = List.of(
                new FieldSpec("ID:", community != null ? String.valueOf(community.getId()) : "", community != null),
                new FieldSpec("Название:", community != null ? community.getName() : ""),
                new FieldSpec("Город:", community != null ? community.getCity() : ""),
                new FieldSpec("Год создания:", community != null ? String.valueOf(community.getBirthYear()) : ""),
                new FieldSpec("Администратор:", community != null ? String.valueOf(community.getAdminId()) : "")
        );

        return GenericEntityDialog.show(owner,
                community == null ? "Добавить сообщество" : "Изменить сообщество",
                fields,
                values -> {
                    int id = Integer.parseInt(values.get(0));
                    String name = values.get(1);
                    String city = values.get(2);
                    int birthYear = Integer.parseInt(values.get(3));
                    int adminId = Integer.parseInt(values.get(4));

                    if (community == null) {
                        return new Community(id, name, city, birthYear, adminId);
                    } else {
                        community.setName(name);
                        community.setCity(city);
                        community.setBirthYear(birthYear);
                        community.setAdminId(adminId);
                        return community;
                    }
                }
        );
    }

    public static FriendShip friendshipDialog(Stage owner, FriendShip friendship) {
        List<FieldSpec> fields = List.of(
                new FieldSpec("Профиль 1:", friendship != null ? String.valueOf(friendship.getProfile1()) : "", friendship != null),
                new FieldSpec("Профиль 2:", friendship != null ? String.valueOf(friendship.getProfile2()) : "", friendship != null),
                new FieldSpec("Сила связи:", friendship != null ? String.valueOf(friendship.getStrength()) : "")
        );

        return GenericEntityDialog.show(
                owner,
                friendship == null ? "Добавить дружбу" : "Изменить дружбу",
                fields,
                values -> {
                    int p1 = Integer.parseInt(values.get(0));
                    int p2 = Integer.parseInt(values.get(1));
                    int strength = Integer.parseInt(values.get(2));

                    if (friendship == null) {
                        return new FriendShip(p1, p2, strength);
                    } else {
                        friendship.setStrength(strength);
                        return friendship;
                    }
                }
        );
    }
}