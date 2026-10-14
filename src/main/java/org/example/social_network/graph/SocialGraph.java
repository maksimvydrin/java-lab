package org.example.social_network.graph;

import org.example.social_network.model.Profile;

public class SocialGraph {

    private static class HashNode {
        int key;
        Profile profile;
        GraphNode head; // Список соседей
        HashNode next;  // Следующий элемент при коллизии

        public HashNode(int key, Profile profile) {
            this.key = key;
            this.profile = profile;
            this.head = null;
            this.next = null;
        }
    }

    private HashNode[] table;
    private int size;
    private static final int CAPACITY = 16;

    public SocialGraph() {
        table = new HashNode[CAPACITY];
        size = 0;
    }

    private int getIndex(int key) {
        return Math.abs(key) % table.length;
    }

    public synchronized void addVertex(Profile profile) {
        if (profile == null) return;
        int key = profile.getId();
        int index = getIndex(key);

        HashNode current = table[index];
        while (current != null) {
            if (current.key == key) {
                current.profile = profile; // Обновляем, если уже есть
                return;
            }
            current = current.next;
        }

        HashNode newNode = new HashNode(key, profile);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
    }

    public synchronized void addEdge(int fromId, int toId, int weight) {
        addToList(fromId, toId, weight);
        addToList(toId, fromId, weight); // Дружба взаимна
    }

    private void addToList(int fromId, int toId, int weight) {
        int index = getIndex(fromId);
        HashNode current = table[index];

        while (current != null && current.key != fromId) {
            current = current.next;
        }

        if (current != null) {
            GraphNode neighbor = current.head;
            while (neighbor != null) {
                if (neighbor.getVertexId() == toId) {
                    neighbor.setWeight(weight); // Обновляем силу связи
                    return;
                }
                neighbor = neighbor.getNext();
            }

            GraphNode newNode = new GraphNode(toId, weight);
            newNode.setNext(current.head);
            current.head = newNode;
        }
    }

    public synchronized GraphNode getNeighbors(int vertexId) {
        int index = getIndex(vertexId);
        HashNode current = table[index];
        while (current != null) {
            if (current.key == vertexId) {
                return current.head;
            }
            current = current.next;
        }
        return null;
    }

    public synchronized int size() {
        return size;
    }

    public synchronized int[] getAllKeys() {
        int[] keys = new int[size];
        int count = 0;
        for (int i = 0; i < table.length; i++) {
            HashNode current = table[i];
            while (current != null) {
                keys[count] = current.key;
                count++;
                current = current.next;
            }
        }
        return keys;
    }
}