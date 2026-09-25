package org.example.social_network.model;

public class DelProfile {
    private final int id;
    private final String name;
    private final String city;
    private final int birthYear;
    private final String delReason;

    public DelProfile(int id, String name, String city, int birthYear, String delReason)
    {
        this.id = id;
        this.name = name;
        this.city = city;
        this.birthYear = birthYear;
        this.delReason = delReason;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getCity()
    {
        return city;
    }

    public int getBirthYear()
    {
        return birthYear;
    }

    public String getDelReason()
    {
        return delReason;
    }

    @Override
    public String toString()
    {
        return "Удалённый профиль: "
                + id + ": "
                + name
                + " (" + city + ", " + birthYear + ")"
                + ", причина: " + delReason;
    }
}