package no.oslomet.meet.classes;

public class Requests
{
    public int id_userSend;
    public int id_userMatch;
    public int requestState;
    public User requestUser;

    public Requests(int id_userSend, int id_userMatch, int requestState, User requestUser)
    {
        this.id_userSend = id_userSend;
        this.id_userMatch = id_userMatch;
        this.requestState = requestState;
        this.requestUser = requestUser;
    }



}
