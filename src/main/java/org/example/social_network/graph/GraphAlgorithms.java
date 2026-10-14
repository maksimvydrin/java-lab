package org.example.social_network.graph;

import java.util.*;

public class GraphAlgorithms {

    public List<Integer> bfs(SocialGraph graph, int start, int target) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> previous = new HashMap<>();

        queue.add(start);
        visited.add(start);

        boolean found = false;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == target) {
                found = true;
                break;
            }

            GraphNode neighbor = graph.getNeighbors(current);
            while (neighbor != null) {
                int neighborId = neighbor.getVertexId();
                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    previous.put(neighborId, current);
                    queue.add(neighborId);
                }
                neighbor = neighbor.getNext();
            }
        }

        if (!found) {
            return new ArrayList<>();
        }

        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != start) {
            path.add(curr);
            curr = previous.get(curr);
        }
        path.add(start);
        Collections.reverse(path);

        return path;
    }

    public List<Integer> dijkstra(SocialGraph graph, int start, int target) {
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        int[] keys = graph.getAllKeys();
        for (int i = 0; i < keys.length; i++) {
            distances.put(keys[i], Integer.MAX_VALUE);
        }

        if (!distances.containsKey(start) || !distances.containsKey(target)) {
            return new ArrayList<>();
        }

        distances.put(start, 0);

        while (visited.size() < keys.length) {
            int closestVertex = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int i = 0; i < keys.length; i++) {
                int v = keys[i];
                if (!visited.contains(v) && distances.get(v) < minDistance) {
                    minDistance = distances.get(v);
                    closestVertex = v;
                }
            }

            if (closestVertex == -1 || minDistance == Integer.MAX_VALUE) {
                break;
            }

            visited.add(closestVertex);

            if (closestVertex == target) {
                break;
            }

            GraphNode neighbor = graph.getNeighbors(closestVertex);
            while (neighbor != null) {
                int nextVertex = neighbor.getVertexId();
                int weight = neighbor.getWeight();

                if (!visited.contains(nextVertex)) {
                    int newDist = distances.get(closestVertex) + weight;
                    if (newDist < distances.get(nextVertex)) {
                        distances.put(nextVertex, newDist);
                        previous.put(nextVertex, closestVertex);
                    }
                }
                neighbor = neighbor.getNext();
            }
        }

        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != start) {
            if (!previous.containsKey(curr)) {
                return new ArrayList<>();
            }
            path.add(curr);
            curr = previous.get(curr);
        }
        path.add(start);
        Collections.reverse(path);

        return path;
    }
}