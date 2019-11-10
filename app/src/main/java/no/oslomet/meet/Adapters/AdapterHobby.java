package no.oslomet.meet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import no.oslomet.meet.R;
import no.oslomet.meet.classes.Hobbies;

public class AdapterHobby extends BaseAdapter
{
    private Context context;
    private ArrayList<Hobbies> items;

    private HobbyAdapterListener listener = null;

    public void setListener(HobbyAdapterListener listener) {
        this.listener = listener;
    }

    public AdapterHobby(Context context, ArrayList<Hobbies> items)
    {
        this.context = context;
        this.items = items;
    }

    public void add(Hobbies hobbies)
    {
        items.add(hobbies);
        notifyDataSetChanged();
    }
    public void remove(int id)
    {
        items.remove(id);
        notifyDataSetChanged();
    }

    public ArrayList<Hobbies> getItems() {
        return items;
    }

    public void addIfNotPresent(Hobbies hobbies)
    {
        boolean isPresent = false;
        for (Hobbies hobby : items)
        {
            if (hobby.getName().equals(hobbies.getName()))
                isPresent = true;
        }
        if (!isPresent)
        {
            add(hobbies);
        }
    }

    public void swapItems(ArrayList<Hobbies> swap)
    {
        items = swap;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_single_line, parent, false);
        ((TextView)convertView.findViewById(R.id.adapter_TextView)).setText(items.get(position).getName());
        ImageButton btnDelete = convertView.findViewById(R.id.imgDeleteLangHobbies);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null)
                    listener.onDeleteClick(items.get(position),position);
            }
        });

        return convertView;
    }

    public interface HobbyAdapterListener{
        void onDeleteClick(Hobbies hobby, int position);
    }
}
