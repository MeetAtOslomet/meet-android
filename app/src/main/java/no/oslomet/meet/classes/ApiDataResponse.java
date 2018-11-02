package no.oslomet.meet.classes;

public class ApiDataResponse
{
    public boolean status;
    public String data;
    public int dataExit;
    public String message;

    public ApiDataResponse(boolean status, String data, int dataExit, String message)
    {
        this.status = status;
        this.data = data;
        this.dataExit = dataExit;
        this.message = message;
    }


}
