package no.oslomet.meet.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import no.oslomet.meet.R;
import no.oslomet.meet.classes.Messaging;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder
{
    TextView name, text, time;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.senderName);
        text = (TextView) itemView.findViewById(R.id.text);
    }

    public void bind(Messaging msg, String fullname)
    {
        name.setText(fullname);
        text.setText(msg.message);
    }
}
