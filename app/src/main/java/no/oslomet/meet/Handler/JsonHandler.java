package no.oslomet.meet.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import no.oslomet.meet.classes.AuthStatus;
import no.oslomet.meet.classes.Heartbeat;
import no.oslomet.meet.classes.Registration;

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
}
