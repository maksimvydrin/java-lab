package org.example.social_network.serialization;

import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
        int count = 10_000;
        int runs = 5;
        Random rnd = new Random(42);

        Profile[] profiles = new Profile[count];
        FriendShip[] friendships = new FriendShip[count - 1]; // Связей будет на 1 меньше

        Community[] communities = new Community[] {
                new Community(1, "Java Devs", "Новосибирск", 2020, 1)
        };
        DelProfile[] deleted = new DelProfile[] {
                new DelProfile(99999, "Удаленный", "Москва", 1995, 15, "Спам")
        };

        for (int i = 0; i < count; i++) {
            int id = i + 1;
            profiles[i] = new Profile(id, "User_" + id, "City_" + (id % 50), 1970 + (id % 50));

            if (id > 1) {
                friendships[i - 1] = new FriendShip(id, rnd.nextInt(id - 1) + 1, 1 + rnd.nextInt(10));
            }
        }

        SocialNetworkData data = new SocialNetworkData(profiles, communities, deleted, friendships);

        runBenchmark(new JsonSocialSerializer(), data, runs);
        runBenchmark(new BinSocialSerializer(), data, runs);
    }

    private static void runBenchmark(Serializer<SocialNetworkData> serializer, SocialNetworkData data, int runs) throws Exception {
        Path tempFile = Files.createTempFile("benchmark_10k_", "." + serializer.format());
        long totalSaveNs = 0;
        long totalLoadNs = 0;

        for (int i = 0; i < runs; i++) {
            long startSave = System.nanoTime();
            serializer.save(data, tempFile);
            totalSaveNs += (System.nanoTime() - startSave);

            long startLoad = System.nanoTime();
            serializer.load(tempFile);
            totalLoadNs += (System.nanoTime() - startLoad);
        }

        long sizeBytes = Files.size(tempFile);
        double avgSaveMs = (totalSaveNs / (double) runs) / 1_000_000.0;
        double avgLoadMs = (totalLoadNs / (double) runs) / 1_000_000.0;

        System.out.printf("Формат: %s%n", serializer.format().toUpperCase());
        System.out.printf("Размер файла (10^4 записей): %d байт (%.2f КБ)%n", sizeBytes, sizeBytes / 1024.0);
        System.out.printf("Среднее время сохранения (5 прогонов): %.2f мс%n", avgSaveMs);
        System.out.printf("Среднее время загрузки (5 прогонов): %.2f мс%n", avgLoadMs);

        Files.deleteIfExists(tempFile);
    }
}