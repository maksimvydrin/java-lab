package org.example.social_network.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.social_network.model.Profile;

import java.io.IOException;
import java.util.List;

public final class MigrationManager {

    private MigrationManager() {}

    public static ObjectNode migrateJsonV1toV2(ObjectNode root) throws IOException {
        root.remove("version");
        root.put("schema_version", 2);
        JsonNode profiles = root.get("profiles");
        if (profiles != null && profiles.isArray()) {
            for (JsonNode p : profiles) {
                if (p.isObject()) {
                    ObjectNode obj = (ObjectNode) p;
                    if (!obj.has("rating") || obj.get("rating").isNull()) {
                        obj.put("rating", 0.0);
                    } else if (!obj.get("rating").isNumber()) {
                        throw new IOException("Ошибка миграции v1 в v2: поле 'rating' должно быть числом");
                    }
                }
            }
        }
        return root;
    }

    public static ObjectNode migrateJsonV2toV3(ObjectNode root) throws IOException {
        root.remove("version");
        root.put("schema_version", 3);
        JsonNode profiles = root.get("profiles");
        if (profiles != null && profiles.isArray()) {
            for (JsonNode p : profiles) {
                if (p.isObject()) {
                    ObjectNode obj = (ObjectNode) p;
                    if (obj.has("rating")) {
                        if (!obj.get("rating").isNumber()) {
                            throw new IOException("Ошибка миграции v2 в v3: поле 'rating' должно быть числом");
                        }
                        double rating = obj.get("rating").asDouble();
                        if (rating < 0.0 || rating > 10.0) {
                            throw new IOException("Ошибка миграции v2 в v3: некорректный rating=" + rating);
                        }
                    }
                    if (!obj.has("note") || obj.get("note").isNull()) {
                        obj.put("note", "");
                    }
                }
            }
        }
        return root;
    }

    public static ObjectNode downgradeJsonV3toV2(ObjectNode root) {
        root.remove("version");
        root.put("schema_version", 2);
        JsonNode profiles = root.get("profiles");
        if (profiles != null && profiles.isArray()) {
            for (JsonNode p : profiles) {
                if (p.isObject()) {
                    ((ObjectNode) p).remove("note");
                }
            }
        }
        return root;
    }

    public static ObjectNode downgradeJsonV2toV1(ObjectNode root) {
        root.remove("version");
        root.put("schema_version", 1);
        JsonNode profiles = root.get("profiles");
        if (profiles != null && profiles.isArray()) {
            for (JsonNode p : profiles) {
                if (p.isObject()) {
                    ((ObjectNode) p).remove("rating");
                }
            }
        }
        return root;
    }

    public static void migrateObjectV1toV2(SocialNetworkData data) throws IOException {
        data.setVersion(2);
        if (data.getProfiles() != null) {
            for (Profile p : data.getProfiles()) {
                if (Double.isNaN(p.getRating()) || p.getRating() < 0.0 || p.getRating() > 10.0) {
                    throw new IOException("Ошибка миграции BIN v1 в v2: некорректный rating=" + p.getRating());
                }
            }
        }
    }

    public static void migrateObjectV2toV3(SocialNetworkData data) throws IOException {
        data.setVersion(3);
        if (data.getProfiles() != null) {
            for (Profile p : data.getProfiles()) {
                if (p.getNote() == null) {
                    p.setNote("");
                }
            }
        }
    }

    public static SocialNetworkData createDowngradedCopy(SocialNetworkData original, int targetVersion) {
        Profile[] origProfiles = original.getProfiles();
        Profile[] copiedProfiles = new Profile[origProfiles.length];

        for (int i = 0; i < origProfiles.length; i++) {
            Profile p = origProfiles[i];
            double rating = (targetVersion >= 2) ? p.getRating() : 0.0;
            String note = (targetVersion >= 3) ? p.getNote() : "";
            copiedProfiles[i] = new Profile(p.getId(), p.getName(), p.getCity(), p.getBirthYear(), rating, note);
        }

        SocialNetworkData copy = new SocialNetworkData(
                copiedProfiles,
                original.getCommunities(),
                original.getDeletedProfiles(),
                original.getFriendships()
        );
        copy.setVersion(targetVersion);
        copy.setTargetVersion(targetVersion);
        return copy;
    }

    public static void validateMigratedData(SocialNetworkData data) throws IOException {
        if (data.getProfiles() == null) return;
        for (Profile p : data.getProfiles()) {
            List<String> errors = p.validate();
            if (!errors.isEmpty()) {
                throw new IOException("Некорректные данные профиля ID=" + p.getId() + ": " + String.join("; ", errors));
            }
        }
    }
}