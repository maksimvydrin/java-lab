package org.example.social_network.csv;
import org.example.social_network.model.FriendShip;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FriendShipCsvSaver {
    public void save(List<FriendShip> friendships, Path file) {
        try (BufferedWriter writer=Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("profile1;profile2;strength");
            writer.newLine();

            for (FriendShip friendship : friendships)
            {
                writer.write(Integer.toString(friendship.getProfile1()));
                writer.write(";");
                writer.write(Integer.toString(friendship.getProfile2()));
                writer.write(";");
                writer.write(Integer.toString(friendship.getStrength()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи файла: " + file, e);
        }
    }
}
