package org.example.socialnetwork.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Редактируемый производный тип с дополнительным полем administratorId.
 */
public final class Community extends Profile {
    private int administratorId;

    public Community(int id, String name, String city, int birthYear, int administratorId) {
        super(id, name, city, birthYear);
        this.administratorId = administratorId;
    }

    public int getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(int administratorId) {
        this.administratorId = administratorId;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>(super.validate());
        if (administratorId <= 0) {
            errors.add("ID администратора должен быть больше 0.");
        }
        return errors;
    }

    @Override
    public String toString() {
        return getId() + ": " + getName() + " (" + getCity() + "), admin=" + administratorId;
    }
}
