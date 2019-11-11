package no.oslomet.meet.classes;

public class Hobbies
{

    private int id_user;
    private int id_hobby;
    private String name;

    public Hobbies(int id_user, int id_language, String name)
    {
        this.id_user = id_user;
        this.id_hobby = id_language;
        this.name = name;
    }

    public Hobbies() {}

    public int getIdUser()
    {
        return id_user;
    }
    public int getIdHobby() { return id_hobby; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Hobbies{" +
                "id_user=" + id_user +
                ", id_hobby=" + id_hobby +
                ", name='" + name + '\'' +
                '}';
    }
}
