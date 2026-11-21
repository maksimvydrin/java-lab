package org.example.social_network.server;

import org.example.social_network.graph.SocialGraph;
import org.example.social_network.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocialNetworkStore {

    private final List<Profile> profiles = new CopyOnWriteArrayList<>();
    private final List<Community> communities = new CopyOnWriteArrayList<>();
    private final List<DelProfile> deletedProfiles = new CopyOnWriteArrayList<>();
    private final List<FriendShip> friendships = new CopyOnWriteArrayList<>();
    private final SocialGraph<Integer, Profile> graph = new SocialGraph<>();

    private final List<Runnable> changeListeners = new CopyOnWriteArrayList<>();

    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    public void notifyChanged() {
        rebuildGraph();
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }

    public synchronized void rebuildGraph() {
        graph.clear();
        for (Profile p : profiles) {
            graph.addVertex(p.getId(), p);
        }
        for (FriendShip f : friendships) {
            if (graph.containsVertex(f.getProfile1()) && graph.containsVertex(f.getProfile2())
                    && f.getProfile1() != f.getProfile2()) {
                graph.addEdge(f.getProfile1(), f.getProfile2(), f.getStrength());
            }
        }
    }

    public List<Profile> getProfiles() { return profiles; }
    public List<Community> getCommunities() { return communities; }
    public List<DelProfile> getDeletedProfiles() { return deletedProfiles; }
    public List<FriendShip> getFriendships() { return friendships; }
    public SocialGraph<Integer, Profile> getGraph() { return graph; }

    public synchronized void replaceAll(List<Profile> p, List<Community> c,
                                        List<DelProfile> d, List<FriendShip> f) {
        profiles.clear();
        profiles.addAll(p);
        communities.clear();
        communities.addAll(c);
        deletedProfiles.clear();
        deletedProfiles.addAll(d);
        friendships.clear();
        friendships.addAll(f);
        notifyChanged();
    }
}