package no.oslomet.meet.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import java.util.ArrayList;

import no.oslomet.meet.classes.Languages;

public class Helper
{

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
