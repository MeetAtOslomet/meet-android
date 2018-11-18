package no.oslomet.meet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

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

    public AdapterTandem(Context context, ArrayList<Tandem> items)
    {
        this.context = context;
        this.items = items;
        String idUser = new SettingsHandler().getStringSetting(context, R.string.preference_idUser);
        Log.e("User Id", idUser);
        id_user = Integer.parseInt(idUser);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_tandem, parent, false);
        for (int i = 0; i < items.size(); i++)
        {
            ArrayList<User> users = items.get(i).users;
            for (User user : users)
            {
                if (user.getIdUser() != id_user)
                {
                    ((TextView)convertView.findViewById(R.id.userFirstName)).setText(user.getFirstName());
                }
            }
        }

        ImageButton ib = convertView.findViewById(R.id.expandOptions);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hello from expandc", Toast.LENGTH_SHORT).show();
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



        return convertView;
    }
}
