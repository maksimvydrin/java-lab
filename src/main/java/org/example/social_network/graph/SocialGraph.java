
package org.example.social_network.graph;

import org.example.social_network.model.Profile;

public class SocialGraph {

    private static class HashNode {
        int key;
        Profile profile;
        GraphNode head;
        HashNode next;

        HashNode(int key, Profile profile) {
            this.key = key;
            this.profile = profile;
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
        return Math.floorMod(key, table.length);
    }

    private HashNode findNode(int vertexId) {
        if (vertexId <= 0) {
            return null;
        }

        int index = getIndex(vertexId);
        HashNode current = table[index];

        while (current != null) {
            if (current.key == vertexId) {
                return current;
            }
            current = current.next;
        }

        return null;
    }

    public synchronized void clear() {
        table = new HashNode[CAPACITY];
        size = 0;
    }

    public synchronized void addVertex(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Профиль не должен быть null"
            );
        }

        int key = profile.getId();

        if (key <= 0) {
            throw new IllegalArgumentException(
                    "ID профиля должен быть положительным"
            );
        }

        int index = getIndex(key);
        HashNode current = table[index];

        while (current != null) {
            if (current.key == key) {
                current.profile = profile;
                return;
            }
            current = current.next;
        }

        HashNode newNode = new HashNode(key, profile);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
    }

    public synchronized boolean containsVertex(int vertexId) {
        return findNode(vertexId) != null;
    }

    public synchronized void addEdge(
            int fromId,
            int toId,
            int weight
    ) {
        if (fromId <= 0 || toId <= 0) {
            throw new IllegalArgumentException(
                    "ID вершин должны быть положительными"
            );
        }

        if (fromId == toId) {
            throw new IllegalArgumentException(
                    "Вершина не может быть связана сама с собой"
            );
        }

        if (weight < 0) {
            throw new IllegalArgumentException(
                    "Вес ребра не может быть отрицательным"
            );
        }

        HashNode from = findNode(fromId);
        HashNode to = findNode(toId);

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Обе вершины должны существовать в графе"
            );
        }

        // Сначала проверяем обе вершины, затем изменяем граф.
        addToList(from, toId, weight);
        addToList(to, fromId, weight);
    }

    private void addToList(
            HashNode vertex,
            int neighborId,
            int weight
    ) {
        GraphNode current = vertex.head;

        while (current != null) {
            if (current.getVertexId() == neighborId) {
                current.setWeight(weight);
                return;
            }
            current = current.getNext();
        }

        GraphNode newNode = new GraphNode(neighborId, weight);
        newNode.setNext(vertex.head);
        vertex.head = newNode;
    }

    public synchronized GraphNode getNeighbors(int vertexId) {
        HashNode node = findNode(vertexId);
        return node == null ? null : node.head;
    }

    public synchronized int size() {
        return size;
    }

    public synchronized int[] getAllKeys() {
        int[] keys = new int[size];
        int count = 0;

        for (HashNode currentBucket : table) {
            HashNode current = currentBucket;

            while (current != null) {
                keys[count++] = current.key;
                current = current.next;
            }
        }

        return keys;
    }
}
