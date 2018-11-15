package no.oslomet.meet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;

import no.oslomet.meet.Adapters.AdapterMessaging;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.Messaging;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.Tandem;
import no.oslomet.meet.classes.User;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Filters;
import no.oslomet.meet.core.Strings;

public class ActivityChat extends AppCompatActivity {

    private int myid;
    public int tandemId;
    public int receiverId = -1;

    public Tandem t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tandemId = getIntent().getIntExtra("id_tandem", -1);
        String id = new SettingsHandler().getStringSetting(this, R.string.preference_idUser);
        myid = Integer.parseInt(id);

        String tandemJson = getIntent().getStringExtra("tandem_json");
        try {
            t = new JsonHandler().getTandems(tandemJson).get(0);
            if (t.id_user1 != myid)
                receiverId = t.id_user1;
            else if (t.id_user2 != myid)
                receiverId = t.id_user2;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String messageGetRequest = null;
    private void SetMessages(final boolean reload)
    {
        if (messageGetRequest == null)
        {
            String authToken = new SettingsHandler().getStringSetting(ActivityChat.this, R.string.preference_AuthKey);
            ArrayList<PostParam> pp = new ArrayList<>();
            pp.add(new PostParam("id_tandem", String.valueOf(tandemId)));
            pp.add(new PostParam("id_user", String.valueOf(myid)));
            pp.add(new PostParam("authenticationToken", authToken ));
            String jData = new JsonHandler()._toJson(pp);
            messageGetRequest = Strings.Request_ReceiveMessages(authToken) + "&data="+ jData;
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String response = new Api().GET(messageGetRequest);
                final ArrayList<Messaging> msgList = new JsonHandler().getMessages(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView lv = ActivityChat.this.findViewById(R.id.messagingView);
                        if (reload && lv.getAdapter() != null)
                        {
                            AdapterMessaging am = (AdapterMessaging) lv.getAdapter();
                            am.swapItems(msgList);
                        }
                        else
                        {
                            AdapterMessaging am = new AdapterMessaging(ActivityChat.this, msgList);
                            if (lv != null)
                                lv.setAdapter(am);
                        }
                        ScrollToBottom();
                    }
                });
            }
        });

    }

    private void ScrollToBottom()
    {
        final ListView lv = ActivityChat.this.findViewById(R.id.messagingView);
        lv.post(new Runnable() {
            @Override
            public void run() {
                AdapterMessaging adapterMessaging = (AdapterMessaging) lv.getAdapter();
                if (adapterMessaging != null && adapterMessaging.getCount() > 1)
                    lv.setSelection(adapterMessaging.getCount() -1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Api api = new Api();
                String authToken = new SettingsHandler().getStringSetting(ActivityChat.this, R.string.preference_AuthKey);
                ArrayList<PostParam> pp = new ArrayList<>();
                pp.add(new PostParam("id_tandem", String.valueOf(tandemId)));
                pp.add(new PostParam("id_user", String.valueOf(myid)));
                pp.add(new PostParam("authenticationToken", authToken ));
                String json = new JsonHandler()._toJson(pp);
                String url = Strings.Request_ReceiveMessages(authToken) + "&data="+ json;
                String response = api.GET(url);

                Log.e("Messaging", "=>" + response);

                final ArrayList<Messaging> msgList = new JsonHandler().getMessages(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdapterMessaging adapterMessaging = new AdapterMessaging(ActivityChat.this, msgList);
                        ListView lv = ActivityChat.this.findViewById(R.id.messagingView);
                        if (lv != null)
                        {
                            lv.setAdapter(adapterMessaging);
                        }
                    }
                });


            }
        });*/
        SetMessages(false);

        ((SwipeRefreshLayout)findViewById(R.id.PullForRefresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SetMessages(false);
                ((SwipeRefreshLayout)findViewById(R.id.PullForRefresh)).setRefreshing(false);
            }
        });

        findViewById(R.id.messageInput).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(250);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ScrollToBottom();
                            }
                        });
                    }
                });
            }
        });
        findViewById(R.id.messageInput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(250);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ScrollToBottom();
                            }
                        });
                    }
                });

            }
        });


        findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiverId != -1 && receiverId != myid)
                {
                    String message = ((EditText)ActivityChat.this.findViewById(R.id.messageInput)).getText().toString();
                    String authToken = new SettingsHandler().getStringSetting(ActivityChat.this, R.string.preference_AuthKey);

                    ArrayList<PostParam> msg = new ArrayList<>();
                    msg.add(new PostParam("id_userSend", String.valueOf(myid)));
                    msg.add(new PostParam("id_userReceive", String.valueOf(receiverId)));
                    msg.add(new PostParam("id_tandem", String.valueOf(t.id_tandem)));
                    msg.add(new PostParam("authenticationToken", authToken));
                    msg.add(new PostParam("message", message));

                    String data = new JsonHandler()._toJson(msg);

                    final ArrayList<PostParam> pp = new ArrayList<>();
                    pp.add(new PostParam("request", "send_message"));
                    pp.add(new PostParam("authenticationToken", authToken));
                    pp.add(new PostParam("id_user", String.valueOf(myid)));
                    pp.add(new PostParam("data", data));

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Api api = new Api();
                            String params = api.POST_DATA(pp);
                            Log.e("Params", "=> " + params);
                            String response = api.POST(Strings.ApiUrl(), params);
                            ApiDataResponse adr = new JsonHandler().getData(response);
                            if (adr != null && adr.dataExit == 0)
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((EditText)findViewById(R.id.messageInput)).setText("");
                                        SetMessages(true);
                                    }
                                });
                            }
                        }
                    });

                }

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(messageArrived, new IntentFilter(Filters.IntentFilters.MESSAGING_NEW.name()));

    }

    BroadcastReceiver messageArrived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if (Filters.Actions.UPDATE.name().equals(action))
            {
                SetMessages(true);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageArrived);
    }
}
