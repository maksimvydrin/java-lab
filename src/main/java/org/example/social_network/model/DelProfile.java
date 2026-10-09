package org.example.social_network.model;

import java.util.ArrayList;
import java.util.List;

public class DelProfile extends Profile {
    private String delReason;
    private int dayDel;

    public DelProfile(final int id, final String name, final String city, final int birthYear, final int dayDel, final String Reason) {
        super(id, name, city, birthYear);
        this.delReason = Reason;
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
    public List<String> validate(){
        List<String> errors = new ArrayList<>(super.validate());

        if(delReason.isEmpty()){
            errors.add("Причина не может быть пустой");
            }
        if (dayDel <= 0) {
            errors.add("Дата удаления должна быть больше 0");
        }
        return errors;
    }

    @Override
    public String toString() {
        return(getId()+":"+getName()+"("+getCity()+","+getBirthYear()+")"+"причина: "+delReason);
    }
}