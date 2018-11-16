package no.oslomet.meet.core;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

import java.util.Map;

import no.oslomet.meet.ActivityLaunch;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.R;
import no.oslomet.meet.classes.FCMPush_Data;


public class FMS extends FirebaseMessagingService
{
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.e(TAG, remoteMessage.getData().toString());
        Map<String,String> data = remoteMessage.getData();
        String json = data.get("data");
        Log.e(TAG, json);

        try {
            FCMPush_Data dat = new JsonHandler().getFcmPush_data(json);
            Intent intent = null;
            if (dat.target.equals("MESSAGING"))
                intent = new Intent(Filters.IntentFilters.MESSAGING_NEW.name());

            if (intent != null)
            {
                intent.putExtra("action", dat.action);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleNotification(remoteMessage);
    }

    private void handleNotification(RemoteMessage remoteMessage)
    {
        Intent intent = new Intent(this, ActivityLaunch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder ncb = new NotificationCompat.Builder(this, Strings.DEFAULT_CHANNEL_ID)
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        ncb.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(Strings.CHAT_ID, ncb.build());

    }


}
