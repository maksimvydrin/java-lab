package org.example.social_network.model;

import java.util.ArrayList;
import java.util.List;

public class Community extends Profile implements Editable {
    private int adminId;

    public Community(int id, String name, String city, int birthYear, int administratorId) {
        super(id, name, city, birthYear);
        this.adminId = administratorId;
    }

    public int getAdministratorId() {
        return adminId;
    }

    public void setAdministratorId(int administratorId) {
        this.adminId = administratorId;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>(super.validate());

        if (adminId <= 0) {
            errors.add("ID администратора некорректен");
        }

        return errors;
    }

    @Override
    public String toString() {
        return "Сообщество: " + getName() + ", ID администратора =" + adminId;
    }
}
