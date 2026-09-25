package org.example.social_network.model;

import java.util.ArrayList;
import java.util.List;

public class Profile implements Editable {
    private int id;
    private String name;
    private String city;
    private int birthYear;

    public Profile(int id,String name,String city,int birthYear){
        this.id = id;
        this.name = name;
        this.city = city;
        this.birthYear = birthYear;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getCity(){
        return city;
    }

    public int getBirthYear(){
        return birthYear;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setBirthYear(int birthYear){
        this.birthYear = birthYear;
    }

    public List<String> validate() {
        List<String>errors = new ArrayList<>();

        if(id<=0){
            errors.add("ID должно быть > 0");
        }

        if(name.isBlank()){
            errors.add("Имя не может быть пустым");
        }

        if(city.isBlank()){
            errors.add("Название города не может быть пустым");
        }

        if(birthYear>2026 || birthYear<1956){
            errors.add("Введенный год рождения некорректен");
        }

        return errors;
    }

    public String toString(){
        return id+":"+name+"("+city+","+birthYear+")";
    }
}