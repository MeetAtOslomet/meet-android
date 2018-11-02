package no.oslomet.meet.classes;

public class Languages
{
    public int id_language;
    public String name;

    public Languages(int id_language, String name)
    {
        this.id_language = id_language;
        this.name = name;
    }

    public int id_user;
    public int teachOrLearn;
    public Languages(int id_language, String name, int id_user, int teachOrLearn)
    {
        this.id_language = id_language;
        this.name = name;
        this.id_user = id_user;
        this.teachOrLearn = teachOrLearn;
    }
}
