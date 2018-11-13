package no.oslomet.meet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

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

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
        }
    }
}
