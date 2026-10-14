package org.example.social_network.graph;

public class GraphNode {
    private int vertexId;
    private int weight;
    private GraphNode next;

    public GraphNode(int vertexId, int weight) {
        this.vertexId = vertexId;
        this.weight = weight;
        this.next = null;
    }

    public int getVertexId() {
        return vertexId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public GraphNode getNext() {
        return next;
    }

    public void setNext(GraphNode next) {
        this.next = next;
    }
}