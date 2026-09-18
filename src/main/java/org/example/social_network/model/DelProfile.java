package org.example.social_network.model;

public class DelProfile extends Profile{
    private final String delReason;

    public DelProfile(int id, String name, String city, int birth_year,String delReason){
        super(id, name, city, birth_year);
        this.delReason = delReason;
    }

    public String getDelReason(){
        return delReason;
    }

        public String toString(){
            return "Удалён профиль:" + super.toString() + ", причина: "+ delReason;
        }
}
