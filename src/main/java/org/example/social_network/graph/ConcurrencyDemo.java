package org.example.social_network.graph;

import org.example.social_network.model.Profile;

public class ConcurrencyDemo {

    public static void runDemo() {
        SocialGraph graph = new SocialGraph();

        int threadCount = 4;
        int operationsPerThread = 250;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int id = threadIndex * 1000 + j + 1;
                        Profile profile = new Profile(id, "Поток " + threadIndex + " - Юзер " + j, "Город", 2000);

                        graph.addVertex(profile);

                        if (j > 0) {
                            graph.addEdge(id, id - 1, 5);
                        }
                    }
                }
            });
        }

        for (int i = 0; i < threadCount; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Все потоки завершили работу!");
        System.out.println("Ожидаемый размер графа: " + (threadCount * operationsPerThread));
        System.out.println("Фактический размер графа: " + graph.size());
    }
}