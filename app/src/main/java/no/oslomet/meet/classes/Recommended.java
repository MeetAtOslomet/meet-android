package no.oslomet.meet.classes;

import java.util.ArrayList;

public class Recommended
{
    private User user;
    private ArrayList<Languages> languages;
    private ArrayList<Hobbies> hobbies;

    public Recommended(User user, ArrayList<Languages> languages, ArrayList<Hobbies> hobbies)
    {
        this.user = user;
        this.languages = languages;
        this.hobbies = hobbies;
    }

    public Recommended() {}

    public void setUser(User user) {this.user = user;}
    public void setLanguages(ArrayList<Languages> languages) { this.languages = languages; }
    public void setHobbies(ArrayList<Hobbies> hobbies) { this.hobbies = hobbies; }

    public User getUser() {
        return user;
    }

    public ArrayList<Languages> getLanguages() {
        return languages;
    }

    public ArrayList<Hobbies> getHobbies() {
        return hobbies;
    }
}
