package no.oslomet.meet.core;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;

import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.classes.PostParam;

public class FCM
{
    public void GetToken(final String username, final String AuthToken)
    {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful())
                {
                    Log.e("FCM issue", "Could not get instanceId :: => " + task.getException());
                    return;
                }
                FirebaseMessaging.getInstance().subscribeToTopic("global");
                String token = task.getResult().getToken();
                Log.e("FCM TOKEN", token);

                final Api api = new Api();
                ArrayList<PostParam> jp = new ArrayList<>();
                jp.add(new PostParam("username", username));
                jp.add(new PostParam("fmcToken", token));
                jp.add(new PostParam("authenticationToken", AuthToken));
                String json = new JsonHandler()._toJson(jp);
                Log.e("API FCM JSON", json);
                final ArrayList<PostParam> pp = new ArrayList<>();
                pp.add(new PostParam("request", "add_fmc_token"));
                pp.add(new PostParam("data", json));
                pp.add(new PostParam("authenticationToken", AuthToken));
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String resp  = api.POST(Strings.ApiUrl(), api.POST_DATA(pp));
                        Log.e("API FCM", "=> " +resp);
                    }
                });


            }
        });
    }





}
