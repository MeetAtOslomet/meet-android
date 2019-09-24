package no.oslomet.meet.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import no.oslomet.meet.ActivityChat;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.R;
import no.oslomet.meet.classes.Tandem;
import no.oslomet.meet.classes.User;

public class AdapterTandem extends BaseAdapter
{

    private Context context;
    private ArrayList<Tandem> items;
    private int id_user = -1;

    private HashMap<Integer, User> users;

    public AdapterTandem(Context context, ArrayList<Tandem> items)
    {
        this.context = context;
        this.items = items;
        String idUser = new SettingsHandler().getStringSetting(context, R.string.preference_idUser);
        Log.e("User Id", idUser);
        id_user = Integer.parseInt(idUser);
        //setUsers();
    }

    private void setUsers()
    {
        users = new HashMap<>();
        for (Tandem t : items)
        {
            for (User u : t.users)
            {
                if (!users.containsKey(u.getIdUser()))
                {
                    users.put(u.getIdUser(), u);
                }
            }
        }
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_tandem, parent, false);
        }

        Tandem t = items.get(position);
        ArrayList<User> u = t.users;
        for (User _u : u)
        {
            if (_u.getIdUser() != id_user)
            {
                ((TextView)convertView.findViewById(R.id.userFirstName)).setText(_u.getFirstName());
            }
        }
        
        ImageButton ib = convertView.findViewById(R.id.expandOptions);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hello from expand", Toast.LENGTH_SHORT).show();
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tandem t = (Tandem) getItem(position);
                try {
                    String tandemJson = new JsonHandler()._toTandemJSON(t);
                    Intent chatIntent = new Intent(context, ActivityChat.class);
                    chatIntent.putExtra("id_tandem", t.id_tandem);
                    chatIntent.putExtra("tandem_json", tandemJson);
                    context.startActivity(chatIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        final ImageButton PlanMeeting = convertView.findViewById(R.id.planMeet);
        PlanMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                View view = (LayoutInflater.from(context)).inflate(R.layout.popup_event,null);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setView(view);

                final EditText nameOfActivity = (EditText) view.findViewById(R.id.nameOfActivity);
                final EditText location = (EditText) view.findViewById(R.id.location);
                final EditText date = (EditText) view.findViewById(R.id.date);
                final EditText message = (EditText) view.findViewById(R.id.message);

                alertBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("1","TEST OK click");
                    }
                });
                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });


        return convertView;
    }
}
