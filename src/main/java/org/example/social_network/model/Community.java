package org.example.social_network.model;

import java.util.ArrayList;
import java.util.List;


public class Community implements Editable {
    private int id;
    private String name;
    private String city;
    private int birth_year;
    private int adminId;

    public Community(int id, String name, String city, int birthYear, int administratorId){

        this.id = id;
        this.name = name;
        this.city = city;
        this.birth_year = birth_year;
        this.adminId = adminId;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCity() {
        return city;
    }
    public int getBirthYear() {
        return birth_year;
    }
    public int getAdministratorId() {
        return adminId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setBirthYear(int birthYear) {
        this.birth_year = birthYear;
    }
    public void setAdministratorId(int administratorId) {
        this.adminId = administratorId;
    }

    @Override
    public List<String> validate(){
        List<String> errors = new ArrayList<>();

        if (id <= 0) {
            errors.add("ID должен быть положительным");
        }
        if (name.isBlank()) {
            errors.add("название сообщества не может быть пустым");
        }
        if (adminId <= 0) {
            errors.add("ID администратора некорректен");
        }
        return errors;
    }

    @Override
    public String toString() {
        return "Сообщество: " + name + ", ID администратора =" + adminId;
    }
}

