package org.example.social_network.graph;

public class GraphNode<K> {
    private K vertexKey;
    private int weight;
    private GraphNode<K> next;

    public GraphNode(K vertexKey, int weight) {
        this.vertexKey = vertexKey;
        this.weight = weight;
        this.next = null;
    }

    public K getVertexKey() {
        return vertexKey;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public GraphNode<K> getNext() {
        return next;
    }

    public void setNext(GraphNode<K> next) { // Принимаем GraphNode<K>
        this.next = next;
    }
}