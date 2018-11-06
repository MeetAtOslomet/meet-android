package no.oslomet.meet.core;

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


}
