package org.example.social_network.csv;

import org.example.social_network.exception.ErrorCsv;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvSaver {

    public <T> void save(List<T> items, Path file){
        if (items.isEmpty()){
            throw new IllegalArgumentException("Список аргументов для сохранения пуст");
        }

        Object example = items.get(0);

        try(BufferedWriter writer = Files.newBufferedWriter(file,StandardCharsets.UTF_8)){
            writer.write(getHeader(example));
            writer.newLine();

            for(T item:items){
                writer.write(formatRow(item));
                writer.newLine();
            }
        } catch (IOException e){
            throw new RuntimeException("Ошибка записи csv файла");
        }
    }

    private String getHeader(Object example) {
        if (example == null) {
            throw new IllegalArgumentException("Объект не может быть null");
        }
        switch(example.getClass().getSimpleName()){
            case"DelProfile":
                return "id;name;city;birthYear;dayDel;delReason";
            case"Community":
                return "id;name;city;birthYear;adminId";
            case"Profile":
                return "id;name;city;birthYear";
            case"FriendShip":
                return "profile1;profile2;strength";
            default: throw new IllegalArgumentException("Неизвестный тип объекта: " + example.getClass().getName());
        }
    }

    private String formatRow(Object item) {
        if (item == null) {
            return "";
        }
        switch (item.getClass().getSimpleName()) {
            case "DelProfile": {
                DelProfile p = (DelProfile) item;
                return p.getId()+";"+clean(p.getName())+";"+clean(p.getCity()) + ";" + p.getBirthYear() + ";" + p.getDayDel() + ";" + clean(p.getDelReason());
            }
            case "Community": {
                Community c = (Community) item;
                return c.getId() + ";" + clean(c.getName()) + ";" + clean(c.getCity()) + ";" + c.getBirthYear() + ";" + c.getAdminId();
            }
            case "Profile": {
                Profile p = (Profile) item;
                return p.getId() + ";" + clean(p.getName()) + ";" + clean(p.getCity()) + ";" + p.getBirthYear();
            }
            case "FriendShip": {
                FriendShip f = (FriendShip) item;
                return f.getProfile1() + ";" + f.getProfile2() + ";" + f.getStrength();
            }
            default:
                return "";
        }
    }

    private String clean(String value) {
        if (value == null){
            return "";
        }
        return value.replace(";", ",").replace("\r", " ").replace("\n", " ");
    }

}





