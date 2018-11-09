package no.oslomet.meet.classes;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class User
{
    private int id_user;
    private String username;
    private String firstName;
    private String lastName;
    private long age;
    private int type;
    private int gender;
    private int id_campus;
    private String biography;

    private int hideLastName = 0;
    private int hideAge = 0;

    public User (int id_user, String username, String firstName, String lastName, long age, int type, int gender, int id_campus, String biography)
    {
        Log.e("VALUES USER", "Type provided: " + type);
        Log.e("VALUES USER", "Gender provieded: " + gender);
        this.id_user = id_user;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.type = type;
        this.gender = gender;
        this.id_campus = id_campus;
        this.biography = biography;
    }
    public User() {}

    public int getIdUser()
    {
        return id_user;
    }
    public String getUsername()
    {
        return username;
    }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public long getAge() { return age; }
    public int getType() { return type; }
    public int getGender() { return gender; }
    public int getIdCampus() { return id_campus; }
    public String getBiography() { return biography; }

    public int getHideLastName() {return hideLastName;}
    public int getHideAge() {return hideAge;}

    public int getReadableAge()
    {

        Date birth = new Date(age);
        Calendar birthC = Calendar.getInstance(TimeZone.getDefault());
        birthC.setTime(birth);

        Calendar today = Calendar.getInstance();

        int Year = today.get(Calendar.YEAR) - birthC.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < birthC.get(Calendar.MONTH))
            Year--;
        else
        {
            if (today.get(Calendar.MONTH) == birthC.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthC.get(Calendar.DAY_OF_MONTH))
                Year--;
        }

        return Year;
    }


    public void setGender(int i) { gender = i; }
    public void setType(int i) { type = i; }
    public void setHideLastName(int i) {hideLastName = i;}
    public void setHideAge(int i ) { hideAge = i;}

    public void setCampus(int position) {
        id_campus = position;
    }
}
