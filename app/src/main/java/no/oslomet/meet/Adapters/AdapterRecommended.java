package no.oslomet.meet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import no.oslomet.meet.R;
import no.oslomet.meet.classes.Hobbies;
import no.oslomet.meet.classes.Languages;
import no.oslomet.meet.classes.Recommended;
import no.oslomet.meet.core.Helper;
import no.oslomet.meet.core.Measurer;

public class AdapterRecommended extends RecyclerView.Adapter<AdapterRecommended.ViewHolder>
{
    Context context;
    ArrayList<Recommended> items;

    public AdapterRecommended(Context context, ArrayList<Recommended> item)
    {
        this.context = context;
        this.items = item;
    }

    public void addItem(Recommended recommended)
    {
        items.add(recommended);
        notifyDataSetChanged();
    }

    public void removeItem(int position)
    {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public Recommended getItem(int position)
    {
        return items.get(position);
    }


    @NonNull
    @Override
    public AdapterRecommended.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.adapter_recommended_text, viewGroup, false);
        return new AdapterRecommended.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        Recommended rUser = items.get(i);
        String nameAndAge = rUser.getUser().getFirstName() + " " + rUser.getUser().getLastName() + ", " +rUser.getUser().getReadableAge();
        viewHolder.profileNameAndAge.setText(nameAndAge);
        Helper h = new Helper();
        ArrayList<Languages> learn = h.getLearningLanguages(rUser.getLanguages());
        ArrayList<Languages> teach = h.getTeachingLanguages(rUser.getLanguages());

        /*AdapterLanguage adapterLearn = new AdapterLanguage(context, lean);
        AdapterLanguage adapterTeach = new AdapterLanguage(context, teach);*/

        /*ArrayAdapter<String> learnArray = new Helper()._toLanguageArrayAdapterSlim(context, lean);
        ArrayAdapter<String> teachArray = new Helper()._toLanguageArrayAdapterSlim(context, teach);*/

        String hobbies = "";
        Hobbies lastHobby = (rUser.getHobbies().size() > 0) ? rUser.getHobbies().get(rUser.getHobbies().size() -1) : null;
        if (lastHobby != null)
        {
            for (Hobbies hobby : rUser.getHobbies())
            {
                if (hobby == lastHobby)
                {
                    hobbies += hobby.getName();
                }
                else
                {
                    hobbies += hobby.getName() + ", ";
                }
            }
        }
        else if (rUser.getHobbies().size() > 0)
        {
            hobbies = rUser.getHobbies().get(0).getName();
        }

        String learnString = "";
        Languages lastLearn = (learn.size() > 0) ? learn.get(learn.size() -1) : null;
        for (Languages lang : learn)
        {
            if (lang == lastLearn)
            {
                learnString += lang.name;
            }
            else
            {
                learnString += lang.name + ", ";
            }
        }

        String teachString = "";
        Languages lastTeach = (teach.size() > 0) ? teach.get(teach.size() -1) : null;
        for (Languages lang : teach)
        {
            if (lang == lastTeach)
            {
                teachString += lang.name;
            }
            else
            {
                teachString += lang.name + ", ";
            }
        }


        viewHolder.RecommendedTeach.setText(teachString);
        viewHolder.RecommendedLearn.setText(learnString);
        viewHolder.profileHobbies.setText(hobbies);
        viewHolder.profileBio.setText(rUser.getUser().getBiography());
        /*viewHolder.languageLearn.setAdapter(learnArray);
        viewHolder.languageTeach.setAdapter(teachArray);*/


        Measurer m = new Measurer(context);

        int height = m.get_DisplayHeight(context);
        height -= new Measurer(context).convertDpToPx(153);

        ViewGroup.LayoutParams lp = viewHolder.CardRoot.getLayoutParams();
        lp.height = height;
        viewHolder.CardRoot.setLayoutParams(lp);




    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView CardRoot;
        TextView profileNameAndAge;
        /*ListView languageLearn;
        ListView languageTeach;*/
        TextView profileHobbies;
        TextView profileBio;
        TextView RecommendedTeach;
        TextView RecommendedLearn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CardRoot = (CardView)itemView.findViewById(R.id.CardRoot);
            profileNameAndAge = (TextView)itemView.findViewById(R.id.profileNameAndAge);
           /* languageLearn = (ListView)itemView.findViewById(R.id.ListView_recommended1);
            languageTeach = (ListView)itemView.findViewById(R.id.ListView_recommended0);*/
            profileHobbies = (TextView)itemView.findViewById(R.id.profileHobbies);
            profileBio = (TextView)itemView.findViewById(R.id.profileBio);
            RecommendedLearn = (TextView)itemView.findViewById(R.id.RecommendedLearn);
            RecommendedTeach = (TextView)itemView.findViewById(R.id.RecommendedTeach);
            RecommendedTeach = (TextView)itemView.findViewById(R.id.RecommendedTeach);
        }
    }


}
