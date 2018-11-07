package no.oslomet.meet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import no.oslomet.meet.R;
import no.oslomet.meet.classes.Hobbies;

public class AdapterHobby extends BaseAdapter
{
    private Context context;
    private ArrayList<Hobbies> items;

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

    public void addIfNotPresent(Hobbies hobbies)
    {
        boolean isPresent = false;
        for (Hobbies lang : items)
        {
            if (hobbies.getName().equals(hobbies.getName()))
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_single_line, parent, false);
        ((TextView)convertView.findViewById(R.id.adapter_TextView)).setText(items.get(position).getName());
        return convertView;
    }
}
