
package org.example.social_network.model;

import java.util.ArrayList;
import java.util.List;

public class DelProfile extends Profile {

    private String delReason;
    private int dayDel;

    public DelProfile(
            int id,
            String name,
            String city,
            int birthYear,
            int dayDel,
            String reason
    ) {
        super(id, name, city, birthYear);
        this.delReason = reason;
        this.dayDel = dayDel;
    }

    public String getDelReason() {
        return delReason;
    }

    public int getDayDel() {
        return dayDel;
    }

    public void setDayDel(int dayDel) {
        this.dayDel = dayDel;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>(super.validate());

        if (delReason == null || delReason.isBlank()) {
            errors.add("Причина удаления не может быть пустой");
        }

        if (dayDel <= 0) {
            errors.add("Дата удаления должна быть больше 0");
        }

        return errors;
    }

    @Override
    public String toString() {
        return getId() + ":" + getName() + "(" + getCity() + "," + getBirthYear() + ")" + " причина: " + delReason;
    }
}
