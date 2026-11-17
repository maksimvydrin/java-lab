package org.example.social_network.serialization;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;

public interface Serializer<T> {
    String format();
    void save(T data, Path file, int version) throws IOException;
    T load(Path file, BiFunction<Integer, Integer, Boolean> migrationApprover) throws IOException;
}   