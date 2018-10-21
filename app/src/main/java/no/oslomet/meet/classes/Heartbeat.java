package no.oslomet.meet.classes;

public class Heartbeat
{
    public boolean Status;
    public long timestamp;

    public Heartbeat(boolean Status, long timestamp)
    {
        this.Status = Status;
        this.timestamp = timestamp;
    }

}
