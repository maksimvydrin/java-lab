
package org.example.social_network.model;

import java.util.ArrayList;
import java.util.List;

public final class DelProfile{

    private final int id;
    private final String name;
    private final String city;
    private final int birthYear;
    private final String delReason;
    private final int dayDel;

    public DelProfile(int id, String name, String city, int birthYear, int dayDel, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Причина удаления не может быть пустой");
        }
        if (dayDel <= 0) {
            throw new IllegalArgumentException("Дата удаления должна быть больше 0");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть больше 0");
        }

        this.id = id;
        this.name = name;
        this.city = city;
        this.birthYear = birthYear;
        this.delReason = reason;
        this.dayDel = dayDel;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public String getCity() {return city;}
    public int getBirthYear() {return birthYear;}
    public int getDayDel() {return dayDel;}
    public String getDelReason() {return delReason;}

    @Override
    public String toString() {
        return getId() + ":" + getName() + "(" + getCity() + "," + getBirthYear() + ")" + " причина: " + delReason;
    }
}
