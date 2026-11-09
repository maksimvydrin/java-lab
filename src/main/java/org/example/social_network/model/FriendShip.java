package org.example.social_network.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendShip implements Editable, Serializable {
    private static final long serialVersionUID = 1L;

    private int profile1;
    private int profile2;
    private int strength;

    public FriendShip() {
    }

    public FriendShip(int profile1, int profile2, int strength) {
        this.profile1 = profile1;
        this.profile2 = profile2;
        this.strength = strength;
    }

    public int getProfile1() { return profile1; }
    public int getProfile2() { return profile2; }
    public int getStrength() { return strength; }

    public void setProfile1(int profile1) { this.profile1 = profile1; }
    public void setProfile2(int profile2) { this.profile2 = profile2; }
    public void setStrength(int strength) { this.strength = strength; }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (profile1 <= 0) errors.add("ID первого профиля должен быть положительным");
        if (profile2 <= 0) errors.add("ID второго профиля должен быть положительным");
        if (profile1 == profile2) errors.add("Профиль не может дружить сам с собой");
        if (strength < 0) errors.add("Сила связи не может быть отрицательной");
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendShip that = (FriendShip) o;
        return profile1 == that.profile1 && profile2 == that.profile2 && strength == that.strength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile1, profile2, strength);
    }
}