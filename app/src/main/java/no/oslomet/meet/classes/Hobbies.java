package no.oslomet.meet.classes;

public class Hobbies
{

    private int id_user;
    private int id_language;
    private String name;

    public Hobbies(int id_user, int id_language, String name)
    {
        this.id_user = id_user;
        this.id_language = id_language;
        this.name = name;
    }

    public int getIdUser()
    {
        return id_user;
    }
    public int getIdLanguage() { return id_language; }
    public String getName() { return name; }
}
