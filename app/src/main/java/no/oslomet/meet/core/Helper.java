package no.oslomet.meet.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

import no.oslomet.meet.classes.Hobbies;
import no.oslomet.meet.classes.Languages;

public class Helper
{

    public ArrayList<Hobbies> extractAvailableHobbies(ArrayList<Hobbies> listed, ArrayList<Hobbies> selected)
    {
        ArrayList<Hobbies> out = new ArrayList<>();

        for (Hobbies h : listed)
        {
           boolean addHobbies = true;
           for (Hobbies s : selected)
           {
               if (h.getIdHobby() == s.getIdHobby())
               {
                   addHobbies = false;
                   break;
               }
           }
            if (addHobbies)
                out.add(h);
        }
        return out;
    }

    public ArrayList<Languages> getTeachingLanguages(ArrayList<Languages> selected)
    {
        ArrayList<Languages> out = new ArrayList<>();

        for (Languages l : selected)
        {
            if (l.teachOrLearn == 0 || l.teachOrLearn == 2)
                out.add(l);
        }
        return out;
    }

    public ArrayList<Languages> getLearningLanguages(ArrayList<Languages> selected)
    {
        ArrayList<Languages> out = new ArrayList<>();

        for (Languages l : selected)
        {
            if (l.teachOrLearn == 1 || l.teachOrLearn == 2)
                out.add(l);
        }
        return out;
    }


    public ArrayList<Languages> extractAvailableForLearning(ArrayList<Languages> listed, ArrayList<Languages> selected)
    {
        ArrayList<Languages> out = new ArrayList<>();

        for(Languages l : listed)
        {
            boolean addLanguage = true;
            for (Languages s : selected)
            {
                if (l.id_language == s.id_language)
                {
                    if (s.teachOrLearn == 1 || s.teachOrLearn == 2)
                    {
                        addLanguage = false;
                        break;
                    }
                }
            }
            if (addLanguage)
                out.add(l);
        }
        return out;
    }

    public ArrayList<Languages> extractAvailableForTeaching(ArrayList<Languages> listed, ArrayList<Languages> selected)
    {
        ArrayList<Languages> out = new ArrayList<>();
        for(Languages l : listed)
        {
            boolean addLanguage = true;
            for (Languages s : selected)
            {
                if (l.id_language == s.id_language)
                {
                    if (s.teachOrLearn == 0 || s.teachOrLearn == 2)
                    {
                        addLanguage = false;
                        break;
                    }
                }
            }
            if (addLanguage)
                out.add(l);
        }
        return out;
    }

    public ArrayAdapter<String> _toLanguageArrayAdapter(Context context, ArrayList<Languages> listed)
    {
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.select_dialog_singlechoice);
        for(Languages l : listed)
        {
            adapter.add(l.name);
        }
        return adapter;
    }

    public ArrayAdapter<String> _toLanguageArrayAdapterSlim(Context context, ArrayList<Languages> listed)
    {
        ArrayAdapter adapter = new ArrayAdapter(context,  android.R.layout.simple_spinner_item);
        for(Languages l : listed)
        {
            adapter.add(l.name);
        }
        return adapter;
    }


    public ArrayAdapter<String> _toHobbyArrayAdapter(Context context, ArrayList<Hobbies> listed)
    {
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.select_dialog_singlechoice);
        for (Hobbies h : listed)
        {
            adapter.add(h.getName());
        }
        return adapter;
    }

    public ArrayList<Languages> mergeLanguages(ArrayList<Languages> learn, ArrayList<Languages> teach, int id_user)
    {
        ArrayList<Languages> out = new ArrayList<>();
        for(Languages l : learn)
        {
            l.teachOrLearn = 1;
            out.add(l);
        }

        for(Languages l : teach)
        {
            boolean add = true;
            for (Languages ll : out)
            {
                if (ll.id_language == l.id_language)
                {
                    add = false;
                    ll.teachOrLearn = 2;
                    break;
                }
            }
            if (add)
            {
                l.teachOrLearn = 0;
                out.add(l);
            }
        }

        for (Languages l : out)
        {
            l.id_user = id_user;
        }

        return out;
    }


    public static void forceTint(Context context, MenuItem menuItem, int ColorId)
    {
        Drawable image = menuItem.getIcon();
        image = DrawableCompat.wrap(image);
        DrawableCompat.setTint(image, ContextCompat.getColor(context.getApplicationContext(), ColorId));
        menuItem.setIcon(image);

        SpannableString sString = new SpannableString(menuItem.getTitle());
        sString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context.getApplicationContext(), ColorId)), 0, sString.length(), 0);
        menuItem.setTitle(sString);
    }

}
