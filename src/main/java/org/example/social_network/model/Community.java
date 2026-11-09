package org.example.social_network.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Community extends Profile implements Editable, Serializable {
    private static final long serialVersionUID = 1L;

    private int adminId;

    public Community() {
        super();
    }

    public Community(int id, String name, String city, int birthYear, int administratorId) {
        super(id, name, city, birthYear);
        this.adminId = administratorId;
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int administratorId) { this.adminId = administratorId; }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (getId() <= 0) errors.add("ID должно быть > 0");
        if (getName() == null || getName().isBlank()) errors.add("Название сообщества не может быть пустым");
        if (getCity() == null || getCity().isBlank()) errors.add("Название города не может быть пустым");
        int currentYear = java.time.Year.now().getValue();
        if (getBirthYear() < 1 || getBirthYear() > currentYear) errors.add("Введённый год создания некорректен");
        if (adminId <= 0) errors.add("ID администратора некорректен");
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Community community = (Community) o;
        return adminId == community.adminId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adminId);
    }

    @Override
    public String toString() {
        return "Сообщество: " + getName() + ", ID администратора =" + adminId;
    }
}