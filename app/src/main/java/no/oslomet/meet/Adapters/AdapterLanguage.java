package no.oslomet.meet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import no.oslomet.meet.R;
import no.oslomet.meet.classes.Languages;

public class AdapterLanguage extends BaseAdapter
{
    public Context context;
    public ArrayList<Languages> items = new ArrayList<>();

    public AdapterLanguage(Context context, ArrayList<Languages> items)
    {
        this.context = context;
        this.items = items;
    }

    public void add(Languages languages)
    {
        items.add(languages);
        notifyDataSetChanged();
    }
    public void remove(int id)
    {
        items.remove(id);
        notifyDataSetChanged();
    }

    public void addIfNotPresent(Languages languages)
    {
        boolean isPresent = false;
        for (Languages lang : items)
        {
            if (lang.name.equals(languages.name))
                isPresent = true;
        }
        if (!isPresent)
        {
            add(languages);
        }
    }

    public void swapItems(ArrayList<Languages> swap)
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_langauge, parent, false);
        ((TextView)convertView.findViewById(R.id.adapter_langauge_TextView)).setText(items.get(position).name);
        return convertView;
    }
}
