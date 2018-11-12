package no.oslomet.meet.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
    public View getView(int position, View convertView, ViewGroup parent) {
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



        return convertView;
    }
}
