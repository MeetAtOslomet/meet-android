package no.oslomet.meet.classes;

public class Registration
{
    public boolean status;
    public String registration;
    public int registrationExit;

    public Registration(boolean status, String registration, int registrationExit)
    {
        this.status = status;
        this.registration = registration;
        this.registrationExit = registrationExit;
    }
}
