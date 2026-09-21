package org.example.social_network.model;

public class FriendShip{
    private final int profile1;
    private final int profile2;
    private int strength;

    public FriendShip(int profile1, int profile2, int strength){
        this.profile1 = profile1;
        this.profile2 = profile2;
        this.strength= strength;
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

    public void setStrength(int strength){
        this.strength = strength;
    }
}

