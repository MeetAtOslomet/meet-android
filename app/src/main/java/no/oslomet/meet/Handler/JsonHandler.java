package no.oslomet.meet.Handler;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.AuthStatus;
import no.oslomet.meet.classes.Campus;
import no.oslomet.meet.classes.Heartbeat;
import no.oslomet.meet.classes.Hobbies;
import no.oslomet.meet.classes.Languages;
import no.oslomet.meet.classes.Messaging;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.Recommended;
import no.oslomet.meet.classes.Registration;
import no.oslomet.meet.classes.Requests;
import no.oslomet.meet.classes.Tandem;
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
        Log.e("OUT API DATA", "=> " + jString);
        ApiDataResponse adr = new ApiDataResponse(false, "dataError", 3, "unhandeled");
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
        Log.e("API Response", jString);
        ArrayList<Languages> languages = new ArrayList<>();
        try
        {
            JSONObject jo = new JSONObject(jString);
            if (jo.isNull("data"))
            {

            }
            else
            {
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

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return languages;
    }

    public ArrayList<Hobbies> getHobbies(String jString)
    {
        Log.e("Hobbies Response", jString);
        ArrayList<Hobbies> out = new ArrayList<>();
        try
        {
            JSONObject jo = new JSONObject(jString);
            if (jo.isNull("data"))
            {

            }
            else
            {
                JSONArray ja = jo.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++)
                {
                    JSONObject el = ja.getJSONObject(i);
                    out.add(new Hobbies(
                            (el.has("id_user")) ? el.getInt("id_user") : -1,
                            (el.has("id_hobbies")) ? el.getInt("id_hobbies") : -1,
                            (el.has("name")) ? el.getString("name") : ""
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;
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

            if (root.has("hide_last_name"))
                user.setHideLastName(root.getInt("hide_last_name"));
            if (root.has("hide_age"))
                user.setHideAge(root.getInt("hide_age"));


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


    public String _toJson(ArrayList<PostParam> items)
    {
        String out = null;
        try
        {
            JSONObject jsonObject = new JSONObject();
            for (PostParam pp : items)
            {
                jsonObject.put(pp.getKey(), pp.getData());
            }
            out = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;
    }

    public String _toLanguageJsonArrayString(String root, ArrayList<Languages> items) throws JSONException {
        String out;

        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        for (Languages l : items)
        {
            JSONObject item = new JSONObject();
            item.put("id_user", l.id_user);
            item.put("id_language", l.id_language);
            item.put("teachOrLearn", l.teachOrLearn);
            ja.put(item);
        }
        jo.put(root, ja);
        out = jo.toString();


        return out;
    }

    public String _toUserJSON(User user) throws JSONException {
        String out = "";
        JSONObject jo = new JSONObject();
        /*jo.put("username", user.getUsername());
        jo.put("first_name", user.getFirstName());
        jo.put("last_name", user.getLastName());
        jo.put("hide_last_name", String.valueOf(user.getHideLastName()));
        jo.put("type", String.valueOf(user.getType()));
        jo.put("gender", String.valueOf(user.getGender()));
        jo.put("age", String.valueOf(user.getAge()));
        jo.put("hide_age", String.valueOf(user.getHideAge()));
        jo.put("id_campus", String.valueOf(user.getIdCampus()));
        jo.put("biography", user.getBiography());*/

        jo.put("username", user.getUsername());
        jo.put("first_name", user.getFirstName());
        jo.put("last_name", user.getLastName());
        jo.put("hide_last_name", user.getHideLastName());
        jo.put("type", user.getType());
        jo.put("gender", user.getGender());
        jo.put("age", user.getAge());
        jo.put("hide_age", user.getHideAge());
        jo.put("id_campus", user.getIdCampus());
        jo.put("biography", user.getBiography());
        out = jo.toString();
        return out;
    }

    public String _toTandemJSON(Tandem tandem) throws JSONException {
        String out = "";
        JSONObject t = new JSONObject();
        t.put("id_tandem", tandem.id_tandem);
        t.put("id_user1", tandem.id_user1);
        t.put("id_user2",tandem.id_user2);
        t.put("conversationName", tandem.conversationName);

        JSONArray jsonArray = new JSONArray();
        for(User user : tandem.users)
        {
            JSONObject ts = new JSONObject(_toUserJSON(user));
            jsonArray.put(ts);
        }
        t.put("users", jsonArray);

        JSONArray ra = new JSONArray();
        ra.put(t);
        JSONObject root = new JSONObject();
        root.put("data", ra);
        out = root.toString();
        return out;
    }



    public ArrayList<Campus> getCampuses(String jString)
    {
        ArrayList<Campus> out = new ArrayList<>();

        Log.e("Campus Response", jString);
        try
        {
            JSONObject jo = new JSONObject(jString);
            if (jo.isNull("data"))
            {

            }
            else
            {
                JSONArray ja = jo.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++)
                {
                    JSONObject el = ja.getJSONObject(i);
                    out.add(new Campus(
                            (el.has("id_campus")) ? el.getInt("id_campus") : -1,
                            (el.has("name")) ? el.getString("name") : ""
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;

    }


    public ArrayList<Recommended> getRecommended(String jString) throws JSONException {
        ArrayList<Recommended> out = new ArrayList<>();

        JSONObject root = new JSONObject(jString);
        JSONArray dataArray = root.getJSONArray("data");

        for(int i = 0; i < dataArray.length(); i++)
        {
            JSONObject aItem = dataArray.getJSONObject(i);
            User user = getUser(aItem.getJSONObject("user").toString());

            JSONArray langs = aItem.getJSONArray("languages");
            JSONObject tempLang = new JSONObject();
            tempLang.put("data", langs);

            JSONArray hobby = aItem.getJSONArray("hobbies");
            JSONObject tempHobby = new JSONObject();
            tempHobby.put("data", hobby);

            out.add(new Recommended(user,
                    getLanguages(tempLang.toString()),
                    getHobbies(tempHobby.toString())
            ));

        }

        return out;
    }

    public int getMatchRequestState(String jString)
    {
        int out = 0;
        try {
            JSONObject root = new JSONObject(jString);
            out = (root.has("requestState")) ?  root.getInt("requestState") : 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;
    }

    public ArrayList<Tandem> getTandems(String jString) throws JSONException {
        ArrayList<Tandem> out = new ArrayList<>();
        JSONObject root = new JSONObject(jString);
        if (root.has("data"))
        {
            JSONArray ja = root.getJSONArray("data");
            for (int i = 0; i < ja.length(); i++)
            {
                JSONObject aE = ja.getJSONObject(i);
                ArrayList<User> users = null;
                if (aE.has("users"))
                {
                    JSONArray userA = aE.getJSONArray("users");
                    JSONObject tempUserObj = new JSONObject();
                    tempUserObj.put("data", userA);
                    users = getUsers(tempUserObj.toString());
                }
                out.add(new Tandem(
                        (aE.has("id_tandem")) ? aE.getInt("id_tandem") : -1,
                        (aE.has("id_user1")) ? aE.getInt("id_user1") : -1,
                        (aE.has("id_user2")) ? aE.getInt("id_user2") : -1,
                        (aE.has("conversationName")) ? aE.getString("conversationName") : "",
                        users
                ));
            }
        }
        return out;
    }

    public ArrayList<Requests> getRequests(String jString) throws JSONException {
        ArrayList<Requests> out = new ArrayList<>();

        JSONObject root = new JSONObject(jString);
        if (root.has("data"))
        {
            JSONArray ja = root.getJSONArray("data");
            if (ja.length() > 0)
            {
                for (int i = 0; i < ja.length(); i++)
                {
                    JSONObject ent = ja.getJSONObject(i);

                    JSONObject usTmp = ent.getJSONObject("user");

                    User user = getUser(usTmp.toString());
                    out.add(new Requests(
                            (ent.has("id_userSend")) ? ent.getInt("id_userSend") : 0,
                            (ent.has("id_userMatch")) ? ent.getInt("id_userMatch") : 0,
                            (ent.has("requestState")) ? ent.getInt("requestState") : 0,
                            user
                    ));

                }
            }
        }

        return out;
    }

    public ArrayList<Messaging> getMessages(String jString){
        ArrayList<Messaging> out = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jString);
            if (root.has("data"))
            {
                JSONArray ja = root.getJSONArray("data");
                for(int i = 0; i < ja.length(); i++)
                {
                    JSONObject en = ja.getJSONObject(i);
                    out.add(new Messaging(
                            (en.has("id_message")) ? en.getInt("id_message") : -1,
                            (en.has("id_userSend")) ? en.getInt("id_userSend") : -1,
                            (en.has("id_userReceive")) ? en.getInt("id_userReceive") : -1,
                            (en.has("id_tandem")) ? en.getInt("id_tandem") : -1,
                            (en.has("dtime")) ? en.getLong("dtime") : -1,
                            (en.has("viewMessage") && !en.isNull("viewMessage")) ? en.getInt("viewMessage") : -1,
                            (en.has("message")) ? en.getString("message") : "Message not available"
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return out;
    }

}
