package no.oslomet.meet.classes;

public class FCMPush_Data
{
    public String target;
    public String action;
    public long timestamp;

    public FCMPush_Data(String target, String action, long timestamp)
    {
        this.target = target;
        this.action = action;
        this.timestamp = timestamp;
    }



}
