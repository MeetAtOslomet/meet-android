package no.oslomet.meet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.R;
import no.oslomet.meet.classes.Messaging;

public class AdapterMessaging extends BaseAdapter
{
    int myId = 0;
    private Context context;
    private ArrayList<Messaging> items;

    //senderName

    public AdapterMessaging(Context context, ArrayList<Messaging> items)
    {
        this.context = context;
        this.items = items;

        String id = new SettingsHandler().getStringSetting(context, R.string.preference_idUser);
        myId = Integer.parseInt(id);
    }

    public void swapItems(ArrayList<Messaging> items)
    {
        this.items = items;
        notifyDataSetChanged();
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
    public View getView(int position, View v, ViewGroup parent)
    {
        Messaging msg = items.get(position);
        if (v == null)
        {
            if (msg.id_userReceive == myId)
            {
                v = LayoutInflater.from(context).inflate(R.layout.view_chat_left, parent, false);
            }
            else
            {
                v = LayoutInflater.from(context).inflate(R.layout.view_chat_right, parent, false);
            }
        }

        ((TextView)v.findViewById(R.id.senderName)).setText("");
        ((TextView)v.findViewById(R.id.text)).setText(msg.message);
        return v;
    }

}
