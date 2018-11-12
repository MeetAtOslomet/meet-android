package no.oslomet.meet.classes;

import java.util.ArrayList;

public class Tandem
{
    public int id_tandem;
    public int id_user1;
    public int id_user2;
    public String conversationName;
    public ArrayList<User> users;

    public Tandem(int id_tandem, int id_user1, int id_user2, String conversationName, ArrayList<User> users)
    {
        this.id_tandem = id_tandem;
        this.id_user1 = id_user1;
        this.id_user2 = id_user2;
        this.conversationName = conversationName;
        this.users = users;
    }


}
