
package org.example.social_network.controller;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.example.social_network.gui.Dialogs;
import org.example.social_network.gui.GenericDialog;
import org.example.social_network.gui.MainApp;
import org.example.social_network.model.*;

public final class Controller {

    public void add(
            Stage stage,
            MainApp.Type type,
            ObservableList<Profile> profiles,
            ObservableList<Community> communities,
            ObservableList<FriendShip> friendships
    ) {
        switch (type) {
            case PROFILE -> addProfile(stage, profiles);
            case COMMUNITY -> addCommunity(stage, communities);
            case FRIENDSHIP -> addFriendship(stage, profiles, friendships);
            case DELETED ->
                    GenericDialog.showAlert(
                            "Удалённые профили нельзя добавлять вручную."
                    );
        }
    }

    public void edit(Stage stage, Object selected) {
        if (selected instanceof Community community) {
            Community candidate = Dialogs.communityDialog(stage, community);

            if (candidate != null) {
                community.setName(candidate.getName());
                community.setCity(candidate.getCity());
                community.setBirthYear(candidate.getBirthYear());
                community.setAdminId(candidate.getAdminId());
            }

        } else if (selected instanceof Profile profile) {
            Profile candidate = Dialogs.profileDialog(stage, profile);

            if (candidate != null) {
                profile.setId(candidate.getId());
                profile.setName(candidate.getName());
                profile.setCity(candidate.getCity());
                profile.setBirthYear(candidate.getBirthYear());
            }

        } else if (selected instanceof FriendShip friendship) {
            FriendShip candidate =
                    Dialogs.friendshipDialog(stage, friendship);

            if (candidate != null) {
                friendship.setStrength(candidate.getStrength());
            }
        }
    }

    private void addProfile(
            Stage stage,
            ObservableList<Profile> profiles
    ) {
        Profile profile = Dialogs.profileDialog(stage, null);

        if (profile != null) {
            boolean duplicate = profiles.stream()
                    .anyMatch(p -> p.getId() == profile.getId());

            if (duplicate) {
                GenericDialog.showAlert(
                        "Профиль с таким ID уже существует."
                );
                return;
            }

            profiles.add(profile);
        }
    }

    private void addCommunity(
            Stage stage,
            ObservableList<Community> communities
    ) {
        Community community = Dialogs.communityDialog(stage, null);

        if (community != null) {
            boolean duplicate = communities.stream()
                    .anyMatch(c -> c.getId() == community.getId());

            if (duplicate) {
                GenericDialog.showAlert(
                        "Сообщество с таким ID уже существует."
                );
                return;
            }

            communities.add(community);
        }
    }

    private void addFriendship(
            Stage stage,
            ObservableList<Profile> profiles,
            ObservableList<FriendShip> friendships
    ) {
        FriendShip friendship = Dialogs.friendshipDialog(stage, null);

        if (friendship == null) {
            return;
        }

        boolean firstExists = profiles.stream()
                .anyMatch(p -> p.getId() == friendship.getProfile1());

        boolean secondExists = profiles.stream()
                .anyMatch(p -> p.getId() == friendship.getProfile2());

        if (!firstExists || !secondExists) {
            GenericDialog.showAlert(
                    "Оба профиля должны существовать."
            );
            return;
        }

        boolean duplicate = friendships.stream().anyMatch(f ->
                (f.getProfile1() == friendship.getProfile1()
                        && f.getProfile2() == friendship.getProfile2())
                ||
                (f.getProfile1() == friendship.getProfile2()
                        && f.getProfile2() == friendship.getProfile1())
        );

        if (duplicate) {
            GenericDialog.showAlert(
                    "Такая дружба уже существует."
            );
            return;
        }

        friendships.add(friendship);
    }
}
