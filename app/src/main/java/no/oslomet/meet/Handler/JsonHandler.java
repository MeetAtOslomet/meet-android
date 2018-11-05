package no.oslomet.meet.Handler;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.AuthStatus;
import no.oslomet.meet.classes.Heartbeat;
import no.oslomet.meet.classes.Languages;
import no.oslomet.meet.classes.Registration;
import no.oslomet.meet.classes.User;

public class JsonHandler
{

    /**
     * Returns Heartbeat object from json string.
     * @param jString JSONObject as string is required
     * @return returns Heartbeat object
     */
    public Heartbeat getHeartbeat(String jString)
    {
        Heartbeat heartbeat = null;
        try
        {
            JSONObject root = new JSONObject(jString);
            heartbeat = new Heartbeat(
                    (root.has("status")) ? root.getBoolean("status") : false,
                    (root.has("timestamp")) ? root.getLong("timestamp") : 0
            );
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return heartbeat;
    }

    public AuthStatus getHasAuth(String jString)
    {
        AuthStatus authStatus = null;
        try
        {
            JSONObject root = new JSONObject(jString);
            authStatus = new AuthStatus(
                    (root.has("status")) ? root.getBoolean("status") : false,
                    (root.has("authentication")) ? root.getString("authentication") : "",
                    (root.has("authenticationExit")) ? root.getInt("authenticationExit") :  -1,
                    (root.has("message")) ? root.getString("message") : "",
                    (root.has("authenticationToken")) ? root.getString("authenticationToken") : ""
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authStatus;
    }


    public Registration getRegistration(String jString)
    {
        Registration registration = null;
        try
        {
            JSONObject root = new JSONObject(jString);
            registration = new Registration(
                    (root.has("status")) ? root.getBoolean("status") : false,
                    (root.has("registration")) ? root.getString("registration") : "",
                    (root.has("registrationExit")) ? root.getInt("registrationExit") :  -1
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return registration;
    }

    public ApiDataResponse getData(String jString)
    {
        ApiDataResponse adr = null;
        try {
            JSONObject root = new JSONObject(jString);
            adr = new ApiDataResponse(
                    (root.has("status") ? root.getBoolean("status") : false),
                    (root.has("data") ? root.getString("data") : ""),
                    (root.has("dataExit") ? root.getInt("dataExit") : -1),
                    (root.has("message") ? root.getString("message"): "")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return adr;
    }

    public ArrayList<Languages> getLanguages(String jString)
    {
        ArrayList<Languages> languages = new ArrayList<>();
        try
        {
            JSONObject jo = new JSONObject(jString);
            JSONArray ja = jo.getJSONArray("data");
            for (int i = 0; i < ja.length(); i++)
            {
                JSONObject l = ja.getJSONObject(i);
                if (l.has("id_user") && l.has("teachOrLearn"))
                {
                    languages.add(new Languages(
                            (l.has("id_language")) ? l.getInt("id_language") : -1,
                            (l.has("name")) ? l.getString("name") : "NAN",
                            (l.has("id_user")) ? l.getInt("id_user") : -1,
                            (l.has("teachOrLearn")) ? l.getInt("teachOrLearn") : -1
                    ));
                }
                else
                {
                    languages.add(new Languages(
                            (l.has("id_language")) ? l.getInt("id_language") : -1,
                            (l.has("name")) ? l.getString("name") : "NAN"
                    ));

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return languages;
    }

    public int getIdUser(String jString)
    {
        int id = -1;

        try {
            JSONObject root = new JSONObject(jString);
            id = (root.has("id_user")) ? root.getInt("id_user") : -1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return id;
    }

    public User getUser(String jString)
    {
        User user = null;
        try {
            JSONObject root = new JSONObject(jString);
            int type = (root.has("type")) ? root.getInt("type") : -1;
            int gender = (root.has("gender")) ? root.getInt("gender") : -1;
            Log.e("VALUES JSON", "Type provided: " + type);
            Log.e("VALUES JSON", "Gender provieded: " + gender);

            user = new User(
                    (root.has("id_user")) ? (root.isNull("id_user") ? -1 : root.getInt("id_user")) : -1,
                    (root.has("username")) ? root.getString("username") : "",
                    (root.has("first_name")) ? root.getString("first_name") : "",
                    (root.has("last_name")) ? root.getString("last_name") : "",
                    (root.has("age")) ? (root.isNull("age") ? -1 : root.getLong("age")) : -1,
                    type,
                    gender,
                    (root.has("id_campus")) ? root.getInt("id_campus") : -1,
                    (root.has("biography")) ? root.getString("biography") : ""
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public ArrayList<User> getUsers(String jString)
    {
        ArrayList<User> users = new ArrayList<>();

        try
        {
            JSONObject root = new JSONObject(jString);
            JSONArray  ja = root.getJSONArray("data");
            for(int i = 0; i < ja.length(); i++)
            {
                JSONObject data = ja.getJSONObject(i);
                users.add(new User(
                        (data.has("id_user")) ? data.getInt("id_user") : -1,
                        (data.has("username")) ? data.getString("username") : "",
                        (data.has("first_name")) ? data.getString("first_name") : "",
                        (data.has("last_name")) ? data.getString("last_name") : "",
                        (data.has("age")) ? (data.isNull("age") ? -1 : data.getLong("age")) : -1,
                        (data.has("type")) ? data.getInt("type") : -1,
                        (data.has("gender")) ? data.getInt("gender") : -1,
                        (data.has("id_campus")) ? data.getInt("id_campus") : -1,
                        (data.has("biography")) ? data.getString("biography") : ""
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return users;
    }



}
