package no.oslomet.meet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentContainer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import no.oslomet.meet.FragmentRecommended;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.R;
import no.oslomet.meet.classes.Requests;

public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.ViewHolder>
{
    private Context context;
    private ArrayList<Requests> items;

    public AdapterRequests(Context context, ArrayList<Requests> items)
    {
        this.items = items;
        this.context = context;
    }


    @NonNull
    @Override
    public AdapterRequests.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_user_bubble, viewGroup, false);
        return new AdapterRequests.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRequests.ViewHolder viewHolder, int i) {
        final int pos = i;
        viewHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do stuff here


                    Intent tandem_request = new Intent(context, FragmentRecommended.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id_user", 4);
                    tandem_request.putExtras(bundle);
                    context.startActivity(tandem_request);

                Toast.makeText(context, "I'm clickable " + String.valueOf(pos), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        RelativeLayout rl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            rl = itemView.findViewById(R.id.request_viewHead);
        }
    }
}
