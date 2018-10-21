package no.oslomet.meet.classes;

public class AuthStatus
{
    public boolean status;
    public String authentication;
    public int authenticationExit;
    public String message;
    public String authenticationToken;

    public AuthStatus(boolean status, String authentication, int authenticationExit, String message, String authenticationToken)
    {
        this.status = status;
        this.authentication = authentication;
        this.authenticationExit = authenticationExit;
        this.message = message;
        this.authenticationToken = authenticationToken;
    }
}
