package org.example.socialnetwork.model;

/**
 * Read-only тип. Такие объекты загружаются из CSV и не редактируются в GUI.
 */
public final class DeletedProfile {
    private final int id;
    private final String name;
    private final String city;
    private final int birthYear;
    private final String reason;

    public DeletedProfile(int id, String name, String city, int birthYear, String reason) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.birthYear = birthYear;
        this.reason = reason;
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
        return birthYear;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (" + city + ", " + birthYear + "), " + reason;
    }
}
