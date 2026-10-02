package org.example.social_network.model;

import java.util.ArrayList;
import java.util.List;

public class FriendShip implements Editable {
    private final int profile1;
    private final int profile2;
    private int strength;

    public FriendShip(int profile1, int profile2, int strength) {
        this.profile1 = profile1;
        this.profile2 = profile2;
        this.strength = strength;
    }

    public int getProfile1() {
        return profile1;
    }

    public int getProfile2() {
        return profile2;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (profile1 <= 0) {
            errors.add("ID первого профиля должен быть положительным");
        }

        if (profile2 <= 0) {
            errors.add("ID второго профиля должен быть положительным");
        }

        if (profile1 == profile2) {
            errors.add("Профиль не может дружить сам с собой");
        }

        if (strength < 0) {
            errors.add("Сила связи не может быть отрицательной");
        }

        return errors;
    }
}