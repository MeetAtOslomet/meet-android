package no.oslomet.meet.classes;

public class PostParam
{
    private String key;
    private String data;
    public PostParam(String key, String data)
    {
        this.key = key;
        this.data = data;
    }

    public String getKey()
    {
        return this.key;
    }
    public String getData()
    {
        return this.data;
    }
}
