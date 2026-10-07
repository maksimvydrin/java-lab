package org.example.socialnetwork.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовая сущность варианта 7.
 */
public class Profile implements Editable {
    private int id;
    private String name;
    private String city;
    private int birthYear;

    public Profile(int id, String name, String city, int birthYear) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.birthYear = birthYear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (id <= 0) {
            errors.add("ID должен быть больше 0.");
        }
        if (name == null || name.isBlank()) {
            errors.add("Имя не должно быть пустым.");
        }
        if (city == null || city.isBlank()) {
            errors.add("Город не должен быть пустым.");
        }
        if (birthYear < 1900 || birthYear > java.time.Year.now().getValue()) {
            errors.add("Некорректный год рождения.");
        }

        return errors;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (" + city + ", " + birthYear + ")";
    }
}
