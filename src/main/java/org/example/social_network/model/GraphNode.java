package org.example.social_network.model;

public class GraphNode {
    private final int ProfileId;
    private GraphNode next;

    public GraphNode(int ProfileId){
    this.ProfileId = ProfileId;
    }

    public int getProfileId() {
        return ProfileId;
    }

    public GraphNode getNext() {
        return next;
    }

    public void setNext(GraphNode next){
        this.next = next;
    }
}

