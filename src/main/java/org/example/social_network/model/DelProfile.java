package org.example.social_network.model;

import java.io.Serializable;
import java.util.Objects;

public final class DelProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String city;
    private int birthYear;
    private String delReason;
    private int dayDel;

    public DelProfile() {
    }

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

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public int getBirthYear() { return birthYear; }
    public int getDayDel() { return dayDel; }
    public String getDelReason() { return delReason; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
    public void setDayDel(int dayDel) { this.dayDel = dayDel; }
    public void setDelReason(String delReason) { this.delReason = delReason; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelProfile that = (DelProfile) o;
        return id == that.id && birthYear == that.birthYear && dayDel == that.dayDel
                && Objects.equals(name, that.name) && Objects.equals(city, that.city)
                && Objects.equals(delReason, that.delReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, city, birthYear, delReason, dayDel);
    }

    @Override
    public String toString() {
        return getId() + ":" + getName() + "(" + getCity() + "," + getBirthYear() + ")" + " причина: " + delReason;
    }
}