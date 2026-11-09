package org.example.social_network.graph;

import java.util.ArrayList;
import java.util.List;

public class SocialGraph<K extends Comparable<K>, V> {

    private static class HashNode<K, V> {
        K key;
        V value;
        GraphNode<K> head;
        HashNode<K, V> next;

        HashNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private HashNode<K, V>[] table;
    private int size;

    private static final int CAPACITY = 16;

    public SocialGraph() {
        table = (HashNode<K, V>[]) new HashNode[CAPACITY];
        size = 0;
    }

    private int getIndex(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % table.length;
    }

    private HashNode<K, V> findNode(K vertexKey) {
        if (vertexKey == null) {
            return null;
        }

        int index = getIndex(vertexKey);
        HashNode<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(vertexKey)) {
                return current;
            }
            current = current.next;
        }

        return null;
    }

        public synchronized void clear() {
        table = (HashNode<K, V>[]) new HashNode[CAPACITY];
        size = 0;
    }

    public synchronized void addVertex(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Ключ и значение не должны быть null");
        }

        int index = getIndex(key);
        HashNode<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        HashNode<K, V> newNode = new HashNode<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
    }

    public synchronized boolean containsVertex(K vertexKey) {
        return findNode(vertexKey) != null;
    }

    public synchronized void addEdge(K fromKey, K toKey, int weight) {
        if (fromKey == null || toKey == null) {
            throw new IllegalArgumentException("Ключи вершин не должны быть null");
        }

        if (fromKey.equals(toKey)) {
            throw new IllegalArgumentException("Вершина не может быть связана сама с собой");
        }

        if (weight < 0) {
            throw new IllegalArgumentException("Вес ребра не может быть отрицательным");
        }

        HashNode<K, V> from = findNode(fromKey);
        HashNode<K, V> to = findNode(toKey);

        if (from == null || to == null) {
            throw new IllegalArgumentException("Обе вершины должны существовать в графе");
        }

        addToList(from, toKey, weight);
        addToList(to, fromKey, weight);
    }

    private void addToList(HashNode<K, V> vertex, K neighborKey, int weight) {
        GraphNode<K> current = vertex.head;

        while (current != null) {
            if (current.getVertexKey().equals(neighborKey)) {
                current.setWeight(weight);
                return;
            }
            current = current.getNext();
        }

        GraphNode<K> newNode = new GraphNode<>(neighborKey, weight);
        newNode.setNext(vertex.head);
        vertex.head = newNode;
    }

    public synchronized GraphNode<K> getNeighbors(K vertexKey) {
        HashNode<K, V> node = findNode(vertexKey);
        return node == null ? null : node.head;
    }

    public synchronized int size() {
        return size;
    }

    public synchronized List<K> getAllKeys() {
        List<K> keys = new ArrayList<>();

        for (HashNode<K, V> currentBucket : table) {
            HashNode<K, V> current = currentBucket;
            while (current != null) {
                keys.add(current.key);
                current = current.next;
            }
        }
        return keys;
    }
}