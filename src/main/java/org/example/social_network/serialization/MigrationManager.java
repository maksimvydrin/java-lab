package org.example.social_network.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.social_network.model.Profile;

    public class MigrationManager {

    //v1 -> v2: добавляем поле rating */
    public static ObjectNode migrateJsonV1toV2(ObjectNode root) {
        root.put("version", 2);
        JsonNode profiles = root.get("profiles");
        if (profiles != null && profiles.isArray()) {
            for (JsonNode p : profiles) {
                if (p.isObject()) {
                    ((ObjectNode) p).put("rating", 0.0); // Заполнение по умолчанию (не null)
                }
            }
        }
        return root;
    }

    //v2 -> v1 (Downgrade): удаляем поле rating */
    public static ObjectNode downgradeJsonV2toV1(ObjectNode root) {
        root.put("version", 1);
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

    //Миграция бинарных объектов */
    public static void migrateObjectV1toV2(SocialNetworkData data) {
        data.setVersion(2);
        if (data.getProfiles() != null) {
            for (Profile p : data.getProfiles()) {
                if (p.getRating() == 0.0) p.setRating(0.0); // Дефолтное
            }
        }
    }

    //Даунгрейд бинарных объектов
    public static void downgradeObjectV2toV1(SocialNetworkData data) {
        data.setVersion(1);
        if (data.getProfiles() != null) {
            for (Profile p : data.getProfiles()) {
                p.setRating(0.0); // Обнуляем новые поля, хотя они запишутся, v1 их проигнорирует
            }
        }
    }
}