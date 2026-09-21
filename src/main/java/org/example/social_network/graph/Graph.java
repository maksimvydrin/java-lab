package org.example.social_network.graph;
import org.example.social_network.model.FriendShip;
import java.util.*;

public class Graph {
    public final Map<Integer,GraphNode>adjacency = new HashMap<>();
    public final Map<String,Integer>strengths = new HashMap<>();

    public void addVertex(int id){
    adjacency.putIfAbsent(id,null);
    }

    public void addFriendShip(FriendShip fs){
        int a = fs.getProfile1();
        int b = fs.getProfile2();

        addVertex(a);
        addVertex(b);

        addToList(a,b);
        addToList(b,a);

        strengths.put(key(a,b), fs.getStrength());
    }

    private void addToList(int from,int to){
        GraphNode node = new GraphNode(to);

        node.setNext(adjacency.get(from));
        adjacency.put(from,node);
    }

    private String key(int a, int b){
        return Math.min(a,b) + ":" + Math.max(a,b);
    }
}
