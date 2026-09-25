package org.example.social_network.graph;

import java.util.*;

public class GraphAlgorithms {
    public List<Integer> bfs(Graph graph, int start, int target){

        Queue<Integer> queue = new LinkedList<>(); //то, что нужно посетить
        Set<Integer> visited = new HashSet<>();  // что посетили
        Map<Integer,Integer> previous = new HashMap<>(); //

        queue.add(start);
        visited.add(start);

        while(!queue.isEmpty()){
            int current = queue.poll();
            if(current==target){
                break;
            }

            GraphNode neighbor = graph.adjacency.get(current); //взяли друга у current

            while(neighbor!=null){
                int neighborId = neighbor.getProfileId();

                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    previous.put(neighborId, current);
                    queue.add(neighborId);
                }
                neighbor = neighbor.getNext();
            }
        }
        return buildPath(previous, start, target);
    }

    private List<Integer>buildPath(Map<Integer,Integer>previous,int start,int target) {
        List<Integer> path = new ArrayList<>();
        int current = target;
        while (current != start) {
            if (!previous.containsKey(current)) {
                return new ArrayList<>();
            }

            path.add(current);
            current = previous.get(current);

        }

        path.add(start);
        Collections.reverse(path);
        return path;
    }
}
