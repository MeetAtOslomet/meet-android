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
import java.util.HashMap;

import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.Holders.ReceivedMessageHolder;
import no.oslomet.meet.Holders.SentMessageHolder;
import no.oslomet.meet.R;
import no.oslomet.meet.classes.Messaging;
import no.oslomet.meet.core.Helper;


public class AdapterMessaging extends RecyclerView.Adapter
{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    private Context context;
    private ArrayList<Messaging> items;
    private HashMap<Integer,String> users;

    public AdapterMessaging(Context context, ArrayList<Messaging> items, HashMap<Integer,String> users)
    {
        this.context = context;
        this.items = items;
        this.users = users;
    }

    public void swapItems(ArrayList<Messaging> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }



    @Override
    public int getItemViewType(int position)
    {
        Messaging msg = items.get(position);
        if (msg.id_userSend == Helper.getMyId(context))
        {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else
        {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v;
        if (viewType == VIEW_TYPE_MESSAGE_SENT)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_right, parent, false);
            return new SentMessageHolder(v);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_left, parent, false);
            return new ReceivedMessageHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        Messaging msg = items.get(position);
        String fullname = (users.containsKey(msg.id_userSend)) ? users.get(msg.id_userSend) : "unknown";

        switch (holder.getItemViewType())
        {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder)holder).bind(msg, "You");
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder)holder).bind(msg, fullname);
        }
    }

}


/*public class AdapterMessaging extends BaseAdapter
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

}*/
