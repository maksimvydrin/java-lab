package org.example.social_network.server;

import org.example.social_network.model.DelProfile;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SocialNetworkRestController {

    private final SocialNetworkService service;

    public SocialNetworkRestController(SocialNetworkService service) {
        this.service = service;
    }

    @GetMapping("/profiles")
    public List<Profile> getProfiles(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Double minRating
    ) {
        return service.findProfiles(city, name, minYear, minRating);
    }

    @GetMapping("/profiles/{id}")
    public Profile getProfile(@PathVariable int id) {
        return service.findProfileById(id);
    }

    @PostMapping("/profiles")
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
        Profile created = service.createProfile(profile);
        return ResponseEntity
                .created(URI.create("/api/profiles/" + created.getId()))
                .body(created);
    }

    @PutMapping("/profiles/{id}")
    public Profile updateProfile(@PathVariable int id, @RequestBody Profile profile) {
        return service.updateProfile(id, profile);
    }

    @DeleteMapping("/profiles/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable int id) {
        service.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public List<DelProfile> getDeleted() {
        return service.findDeleted();
    }

    @PutMapping("/deleted/{id}")
    public DelProfile updateDeleted(@PathVariable int id) {
        return service.updateDeleted(id);
    }

    @PostMapping("/friendships")
    public ResponseEntity<FriendShip> createFriendship(@RequestBody FriendShip fs) {
        FriendShip created = service.createFriendship(fs);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/graph/path")
    public List<Integer> findPath(
            @RequestParam int from,
            @RequestParam int to,
            @RequestParam(defaultValue = "bfs") String algo
    ) {
        return service.findPath(from, to, algo);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportData(
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(defaultValue = "3") int version
    ) throws IOException {
        byte[] bytes = service.exportData(format, version);
        MediaType type = "json".equalsIgnoreCase(format)
                ? MediaType.APPLICATION_JSON
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok().contentType(type).body(bytes);
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importData(
            @RequestParam(defaultValue = "json") String format,
            @RequestBody byte[] body
    ) throws IOException {
        service.importData(format, body);
        return ResponseEntity.ok().build();
    }
}