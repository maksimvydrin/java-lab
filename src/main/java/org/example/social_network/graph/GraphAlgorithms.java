
package org.example.social_network.graph;

import java.util.*;

public class GraphAlgorithms {

    public List<Integer> bfs(SocialGraph graph, int start, int target)
    {
        if (graph == null || !graph.containsVertex(start) || !graph.containsVertex(target)) {
            return new ArrayList<>();
        }

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> previous = new HashMap<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == target) {
                return restorePath(previous, start, target);
            }

            GraphNode neighbor = graph.getNeighbors(current);

            while (neighbor != null) {
                int neighborId = neighbor.getVertexId();

                if (visited.add(neighborId)) {
                    previous.put(neighborId, current);
                    queue.add(neighborId);
                }
                neighbor = neighbor.getNext();
            }
        }
        return new ArrayList<>();
    }

    public List<Integer> dijkstra(
            SocialGraph graph,
            int start,
            int target
    ) {
        if (graph == null || !graph.containsVertex(start) || !graph.containsVertex(target))
        {
            return new ArrayList<>();
        }

        int[] keys = graph.getAllKeys();

        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        for (int key : keys) {
            distances.put(key, Integer.MAX_VALUE);
        }

        distances.put(start, 0);

        while (visited.size() < keys.length) {
            int closestVertex = -1;
            long minDistance = Integer.MAX_VALUE;

            for (int key : keys) {
                long distance = distances.get(key);

                if (!visited.contains(key)
                        && distance < minDistance) {
                    minDistance = distance;
                    closestVertex = key;
                }
            }

            if (closestVertex == -1)
            {
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

                if (weight < 0) {
                    throw new IllegalStateException("Дейкстра не поддерживает отрицательные веса");
                }

                if (!visited.contains(nextVertex)) {
                    int newDistance = distances.get(closestVertex) + weight;

                    if (newDistance < distances.get(nextVertex)){
                        distances.put(nextVertex, newDistance);
                        previous.put(nextVertex, closestVertex);
                    }
                }
                neighbor = neighbor.getNext();
            }
        }

        if (distances.get(target) == Integer.MAX_VALUE)
        {return new ArrayList<>();}
        return restorePath(previous, start, target);
    }

    private List<Integer> restorePath(Map<Integer, Integer> previous, int start, int target)
    {
        List<Integer> path = new ArrayList<>();
        int current = target;

        while (current != start) {
            path.add(current);
            Integer predecessor = previous.get(current);
            if (predecessor == null) {
                return new ArrayList<>();
            }
            current = predecessor;
        }
        path.add(start);
        Collections.reverse(path);

        return path;
    }
}
