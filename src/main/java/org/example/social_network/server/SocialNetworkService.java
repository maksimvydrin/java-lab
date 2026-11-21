package org.example.social_network.server;

import org.example.social_network.graph.GraphAlgorithms;
import org.example.social_network.model.*;
import org.example.social_network.serialization.*;
import org.example.social_network.server.GlobalExceptionHandler.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class SocialNetworkService {

    private final SocialNetworkStore store;
    private final GraphAlgorithms algorithms = new GraphAlgorithms();
    private final JsonSocialSerializer jsonSerializer = new JsonSocialSerializer();
    private final BinSocialSerializer binSerializer = new BinSocialSerializer();

    public SocialNetworkService(SocialNetworkStore store) {
        this.store = store;
    }

    public SocialNetworkStore getStore() {
        return store;
    }

    public void addChangeListener(Runnable listener) {
        store.addChangeListener(listener);
    }

    public List<Profile> findProfiles(String city, String name, Integer minYear, Double minRating) {
        return store.getProfiles().stream()
                .filter(p -> city == null || p.getCity().equalsIgnoreCase(city))
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> minYear == null || p.getBirthYear() >= minYear)
                .filter(p -> minRating == null || p.getRating() >= minRating)
                .toList();
    }

    public Profile findProfileById(int id) {
        return store.getProfiles().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Профиль с ID=" + id + " не найден"));
    }

    public Profile createProfile(Profile profile) {
        validate(profile);
        boolean exists = store.getProfiles().stream().anyMatch(p -> p.getId() == profile.getId());
        if (exists) {
            throw new ConflictException("Профиль с ID=" + profile.getId() + " уже существует");
        }

        store.getProfiles().add(profile);
        store.notifyChanged();
        return profile;
    }

    public Profile updateProfile(int id, Profile updated) {
        Profile existing = findProfileById(id);
        if (!(existing instanceof Editable)) {
            throw new NotEditableException("Запись с ID=" + id + " нельзя редактировать");
        }
        updated.setId(id);
        validate(updated);

        existing.setName(updated.getName());
        existing.setCity(updated.getCity());
        existing.setBirthYear(updated.getBirthYear());
        existing.setRating(updated.getRating());
        existing.setNote(updated.getNote());
        store.notifyChanged();
        return existing;
    }

    public void deleteProfile(int id) {
        Profile existing = findProfileById(id);
        store.getProfiles().remove(existing);
        store.getFriendships().removeIf(f -> f.getProfile1() == id || f.getProfile2() == id);
        store.notifyChanged();
    }

    public List<DelProfile> findDeleted() {
        return store.getDeletedProfiles();
    }

    public DelProfile updateDeleted(int id) {
        boolean exists = store.getDeletedProfiles().stream().anyMatch(d -> d.getId() == id);
        if (!exists) {
            throw new NotFoundException("Удалённый профиль ID=" + id + " не найден");
        }
        throw new NotEditableException("Удалённый профиль (read-only) нельзя редактировать");
    }

    public FriendShip createFriendship(FriendShip fs) {
        validate(fs);
        findProfileById(fs.getProfile1());
        findProfileById(fs.getProfile2());
        store.getFriendships().add(fs);
        store.notifyChanged();
        return fs;
    }

    public List<Integer> findPath(int from, int to, String algo) {
        store.rebuildGraph();
        if ("dijkstra".equalsIgnoreCase(algo)) {
            return algorithms.dijkstra(store.getGraph(), from, to);
        }
        return algorithms.bfs(store.getGraph(), from, to);
    }

    public byte[] exportData(String format, int version) throws IOException {
        Serializer<SocialNetworkData> serializer = resolveSerializer(format);
        SocialNetworkData data = new SocialNetworkData(
                store.getProfiles().toArray(new Profile[0]),
                store.getCommunities().toArray(new Community[0]),
                store.getDeletedProfiles().toArray(new DelProfile[0]),
                store.getFriendships().toArray(new FriendShip[0])
        );
        Path tmp = Files.createTempFile("export_", "." + serializer.format());
        try {
            serializer.save(data, tmp, version);
            return Files.readAllBytes(tmp);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    public void importData(String format, byte[] content) throws IOException {
        Serializer<SocialNetworkData> serializer = resolveSerializer(format);
        Path tmp = Files.createTempFile("import_", "." + serializer.format());
        try {
            Files.write(tmp, content);
            SocialNetworkData data = serializer.load(tmp, (oldV, newV) -> true);
            store.replaceAll(
                    Arrays.asList(data.getProfiles()),
                    Arrays.asList(data.getCommunities()),
                    Arrays.asList(data.getDeletedProfiles()),
                    Arrays.asList(data.getFriendships())
            );
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    private Serializer<SocialNetworkData> resolveSerializer(String format) {
        if ("bin".equalsIgnoreCase(format)) return binSerializer;
        if ("json".equalsIgnoreCase(format)) return jsonSerializer;
        throw new ValidationException("Неподдерживаемый формат: " + format + " (используйте json или bin)");
    }

    private void validate(Editable entity) {
        List<String> errors = entity.validate();
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join("; ", errors));
        }
    }
}