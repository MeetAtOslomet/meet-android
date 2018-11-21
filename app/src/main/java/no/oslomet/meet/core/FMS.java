package no.oslomet.meet.core;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
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
        //Log.e(TAG, "From: " + remoteMessage.getFrom());
        //Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        //Log.e(TAG, remoteMessage.getData().toString());



        String title = "";
        String body = "";
        int NotifyId = Strings.CHAT_ID;
        String type = "";

        Map<String,String> data = null;
        if (remoteMessage.getData() != null)
        {
            data = remoteMessage.getData();
            title = (data.containsKey("title")) ? data.get("title") : "";
            body = (data.containsKey("body")) ? data.get("body") : "";


            String json = data.get("data");
            Log.e(TAG, json);

            try {
                FCMPush_Data dat = new JsonHandler().getFcmPush_data(json);
                NotifyId = ((Long)dat.timestamp).intValue();

                Intent intent = null;
                type = dat.target;
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
        }

        if (remoteMessage.getNotification() != null)
        {
            //Push Contains notification
            handleNotification(title, body, NotifyId, type);
        }
        else if (data != null)
        {
            handleNotification(title, body, NotifyId, type);
        }


    }

    private void handleNotification(String title, String body, int id, String type)
    {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        boolean addToGroup = false;


        Intent intent = new Intent(this, ActivityLaunch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder ncb = new NotificationCompat.Builder(this, Strings.DEFAULT_CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_match_cards);
        ncb.setContentIntent(pendingIntent);

        if (type.equals("MESSAGING"))
        {
            addToGroup = addNotificationGroup(title, Strings.CHAT_GROUP_ID, 1);
            ncb.setGroup(Strings.CHAT_GROUP_ID);
        }




        if (addToGroup)
            NotificationManagerCompat.from(this).notify(id, ncb.build());
        else
            notificationManagerCompat.notify(id, ncb.build());

    }

    private boolean addNotificationGroup(String title, String GroupKey, int GroupId)
    {
        boolean exists = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            StatusBarNotification[] notifications = nm.getActiveNotifications();
            for (StatusBarNotification sbn : notifications)
            {
                if (sbn.getGroupKey() == GroupKey || sbn.getId() == GroupId)
                    exists = true;
            }
            if (exists == false)
            {
                NotificationCompat.Builder group = new NotificationCompat.Builder(this, Strings.DEFAULT_CHANNEL_ID)
                        .setContentTitle(title)
                        .setGroup(GroupKey)
                        .setGroupSummary(true)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setStyle(new NotificationCompat.BigTextStyle());
                NotificationManagerCompat manager = NotificationManagerCompat.from(this);
                manager.notify(GroupId, group.build());
                exists = true;
            }
        }
        return exists;
    }


}
