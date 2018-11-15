package no.oslomet.meet.classes;

public class Messaging
{
    public int id_message;
    public int id_userSend;
    public int id_userReceive;
    public int id_tandem;
    public long dtime;
    public int viewMessage;
    public String message;

    public Messaging(int id_message, int id_userSend, int id_userReceive, int id_tandem, long dtime, int viewMessage, String message)
    {
        this.id_message = id_message;
        this.id_userSend = id_userSend;
        this.id_userReceive = id_userReceive;
        this.id_tandem = id_tandem;
        this.dtime = dtime;
        this.viewMessage = viewMessage;
        this.message = message;
    }


}
