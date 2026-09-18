package org.example.social_network.model;

public class FriendShip {
    private final int profile1;
    private final int profile2;
    private int adjacency;

    public Friends(int profile1, int profile2, int adjacency){
        this.profile1 = profile1;
        this.profile2 = profile2;
        this.adjacency = adjacency;
    }

    public int getProfile1() {
        return profile1;
    }

    public int getProfile2() {
        return profile2;
    }

    public int getAdjacency() {
        return adjacency;
    }

    public void setAdjacency(int adjacency){
        this.adjacency = adjacency;
    }
}

