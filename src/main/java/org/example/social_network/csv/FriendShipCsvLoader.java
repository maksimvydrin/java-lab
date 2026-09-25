package org.example.social_network.csv;
import org.example.social_network.model.FriendShip;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FriendShipCsvLoader{

    public List<FriendShip> load(Path file) {
        List<FriendShip> result = new ArrayList<>();
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(";", -1);
                    if (parts.length != 3)
                    {
                        continue;
                    }
                    int profile1=Integer.parseInt(parts[0]);
                    int profile2=Integer.parseInt(parts[1]);
                    int strength = Integer.parseInt(parts[2]);
                    FriendShip friendShip= new FriendShip(profile1, profile2,strength);
                    result.add(friendShip);
                } catch (NumberFormatException e)
                {
                    System.out.println("Ошибочная строка пропущена");
                }
            }

        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка чтения файла: " + file, e);
        }

        return result;
    }
}