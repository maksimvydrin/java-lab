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

    public List<Integer> deikstra(Graph graph,int start, int target){
        Map<Integer,Integer> distance = new HashMap<>();
        Map<Integer,Integer> previous = new HashMap<>();
        Set<Integer> visited  = new HashSet<>();

        for(Integer vertex: graph.adjacency.keySet()){
            distance.put(vertex,Integer.MAX_VALUE);
        }

        if(!distance.containsKey(start)||!distance.containsKey(target)){
            return new ArrayList<>();
        }

        distance.put(start,0);

        while(visited.size() < graph.adjacency.size()){
            int current = -1;
            int minDistance = Integer.MAX_VALUE;

            for(Integer vertex:distance.keySet()){
                if(!visited.contains(vertex) && distance.get(vertex)<minDistance){
                    current = vertex;
                    minDistance=distance.get(vertex);
                }
            }
            if(current == -1){
                break;
            }
            if(current==target){
                break;
            }
            visited.add(current);
            GraphNode neighbor = graph.adjacency.get(current);
            while(neighbor!=null){
                int next=neighbor.getProfileId();
                String key = Math.min(current,next)+":"+Math.max(current,next);
                int stength = graph.strengths.getOrDefault(key,1);
                if(!visited.contains(next)){
                    int newDistance = distance.get(current)+stength;
                    if(newDistance<distance.get(next)){
                        distance.put(next,newDistance);
                        previous.put(next,current);
                    }
                }
            neighbor = neighbor.getNext();
            }

        }
    return buildPath(previous,start,target);
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
