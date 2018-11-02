package no.oslomet.meet.classes;

public class User
{
    public int id_user;
    public String username;
    public String firstName;
    public String lastName;
    public long age;
    public int type;
    public int gender;
    public int id_campus;
    public String biography;

    public User (int id_user, String username, String firstName, String lastName, long age, int type, int gender, int id_campus, String biography)
    {
        this.id_user = id_user;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.type = type;
        this.gender = gender;
        this.id_campus = id_campus;
        this.biography = biography;
    }

    public User() {}

}
