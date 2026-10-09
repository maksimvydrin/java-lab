package org.example.social_network.controller;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.example.social_network.gui.EntityDialogs;
import org.example.social_network.gui.GenericEntityDialog;
import org.example.social_network.gui.MainApp;
import org.example.social_network.model.*;

public final class EntityController {

    public void add(Stage stage, MainApp.Type type,
                    ObservableList<Profile> profiles,
                    ObservableList<Community> communities,
                    ObservableList<FriendShip> friendships) {
        switch (type) {
            case PROFILE -> addProfile(stage, profiles);
            case COMMUNITY -> addCommunity(stage, communities);
            case FRIENDSHIP -> addFriendship(stage, friendships);
            case DELETED -> GenericEntityDialog.showAlert("Удалённые профили нельзя добавлять.");
        }
    }

    public void edit(Stage stage, Object selected) {
        if (selected instanceof Profile profile) {
            EntityDialogs.profileDialog(stage, profile);
        } else if (selected instanceof Community community) {
            EntityDialogs.communityDialog(stage, community);
        } else if (selected instanceof FriendShip friendship) {
            EntityDialogs.friendshipDialog(stage, friendship);
        }
    }

    private void addProfile(Stage stage, ObservableList<Profile> profiles) {
        Profile profile = EntityDialogs.profileDialog(stage, null);
        if (profile != null) profiles.add(profile);
    }

    private void addCommunity(Stage stage, ObservableList<Community> communities) {
        Community community = EntityDialogs.communityDialog(stage, null);
        if (community != null) communities.add(community);
    }

    private void addFriendship(Stage stage, ObservableList<FriendShip> friendships) {
        FriendShip friendship = EntityDialogs.friendshipDialog(stage, null);
        if (friendship != null) friendships.add(friendship);
    }
}
