package org.example.social_network.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;

import java.util.Random;

public class DataGenerator {
    private Task<Void> generationTask;

    private static final String[] NAMES = {"Аня", "Борис", "Вера", "Глеб", "Дима", "Елена", "Иван", "Мария"};
    private static final String[] CITIES = {"Москва", "Казань", "Санкт-Петербург", "Екатеринбург", "Новосибирск"};

    public void startGeneration(
            ObservableList<Profile> profiles,
            ObservableList<FriendShip> friendships,
            int count
    ) {
        generationTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Random random = new Random();
                int startId = 1;

                if (!profiles.isEmpty()) {
                    int maxId = 0;

                    for (Profile p : profiles) {
                        if (p.getId() > maxId) {
                            maxId = p.getId();
                        }
                    }
                    startId = maxId + 1;
                }


                java.util.List<Profile> profileBatch = new java.util.ArrayList<>();
                for (int i = 0; i < count; i++) {
                    if (isCancelled()) break;

                    int id = startId + i;
                    String name = NAMES[random.nextInt(NAMES.length)];
                    String city = CITIES[random.nextInt(CITIES.length)];
                    int birthYear = 1970 + random.nextInt(50);

                    profileBatch.add(new Profile(id, name, city, birthYear));

                    if (profileBatch.size() == 500 || i == count - 1) {
                        java.util.List<Profile> toAdd = new java.util.ArrayList<>(profileBatch);
                        Platform.runLater(() -> profiles.addAll(toAdd));
                        profileBatch.clear();
                    }
                }


                java.util.List<FriendShip> friendshipBatch = new java.util.ArrayList<>();
                for (int i = 0; i < count; i++) {
                    if (isCancelled()) break;

                    int fromId = startId + i;
                    int edges = 1 + random.nextInt(3);

                    for (int j = 0; j < edges; j++) {
                        int toId = startId + random.nextInt(count);
                        if (fromId != toId) {
                            int strength = 1 + random.nextInt(10);
                            friendshipBatch.add(new FriendShip(fromId, toId, strength));
                        }
                    }

                    if (friendshipBatch.size() >= 500 || i == count - 1) {
                        java.util.List<FriendShip> toAdd = new java.util.ArrayList<>(friendshipBatch);
                        Platform.runLater(() -> friendships.addAll(toAdd));
                        friendshipBatch.clear();
                    }
                }
                return null;
            }
        };

        new Thread(generationTask).start();
    }

    public void cancelGeneration() {
        if (generationTask != null && !generationTask.isDone()) {
            generationTask.cancel();
        }
    }
}