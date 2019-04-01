package no.oslomet.meet.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

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

        setCountries(viewHolder, learn);

        Random rnd = new Random();
        /*int color = Color.argb(75, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewHolder.backgroundHeader.setBackgroundColor(color);*/

        viewHolder.RecommendedTeach.setText(teachString);
        //viewHolder.RecommendedLearn.setText(learnString);
        viewHolder.profileHobbies.setText(hobbies);
        //viewHolder.profileBio.setText(rUser.getUser().getBiography());

        int number = rnd.nextInt(100);
        //Picasso.get().load("https://randomuser.me/api/portraits/men/"+number+".jpg").into(viewHolder.profilePicture);
        viewHolder.scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Disallow the touch request for parent scroll on touch of child view
                view.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        //Fixes the height of the cards. Higher dp means smaller cards.
        Measurer m = new Measurer(context);

        int height = m.get_DisplayHeight(context);
        height -= new Measurer(context).convertDpToPx(195);

        ViewGroup.LayoutParams lp = viewHolder.CardRoot.getLayoutParams();
        lp.height = height;
        viewHolder.CardRoot.setLayoutParams(lp);
    }

    /**
     * @param viewHolder is passed from onBindViewHolder
     * @param learn is an ArrayList<Languages>
     * Displays the languages and their country codes as flags on the matching card.
     * Will check each language up to a match in a predefined String array in Strings.xml
     * Only converts the first three languages of the language list.
     */
    public void setCountries(@NonNull ViewHolder viewHolder, ArrayList<Languages> learn){
        //TODO: CLEAN UP THIS MESSY CODE. INEFFECTIVE CODE.

        String[] languagesXML = context.getResources().getStringArray(R.array.languages_array);
        String[] codesXML = context.getResources().getStringArray(R.array.countryCode_array);

        String code1 = (learn.size() > 0 ? (getCountryCode(learn.get(0), languagesXML, codesXML)) : (null));
        String code2 = (learn.size() > 1 ? (getCountryCode(learn.get(1), languagesXML, codesXML)) : (null));
        String code3 = (learn.size() > 2 ? (getCountryCode(learn.get(2), languagesXML, codesXML)) : (null));

        if(code1 != null){
            Picasso.get().load("https://www.countryflags.io/"+ code1 +"/flat/64.png").into(viewHolder.recommended1);
            viewHolder.recommended1Text.setText(learn.get(0).name);
        }

        if(code2 != null){
            Picasso.get().load("https://www.countryflags.io/"+ code2 +"/flat/64.png").into(viewHolder.recommended2);
            viewHolder.recommended2Text.setText(learn.get(1).name);
        }

        if(code3 != null){
            Picasso.get().load("https://www.countryflags.io/"+ code3 +"/flat/64.png").into(viewHolder.recommended3);
            viewHolder.recommended3Text.setText(learn.get(2).name);
        }
    }

    public String getCountryCode(Languages l, String[] langs, String[] codes){


        int counter = 0;
        for(String s : langs){
            if(l.name.equals(s)){
                return codes[counter];
            }
            counter++;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView CardRoot;
        TextView profileNameAndAge;
        TextView profileHobbies;
        ImageView profilePicture;
        TextView profileBio;
        TextView RecommendedTeach;
        TextView RecommendedLearn;
        ImageView recommended1;
        ImageView recommended2;
        ImageView recommended3;
        TextView recommended1Text;
        TextView recommended2Text;
        TextView recommended3Text;
        View backgroundHeader;
        ScrollView scrollview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CardRoot = (CardView)itemView.findViewById(R.id.CardRoot);
            profileNameAndAge = (TextView)itemView.findViewById(R.id.profileNameAndAge);
            profileHobbies = (TextView)itemView.findViewById(R.id.profileHobbies);
            profileBio = (TextView)itemView.findViewById(R.id.profileBio);
            //RecommendedLearn = (TextView)itemView.findViewById(R.id.RecommendedLearn);
            RecommendedTeach = (TextView)itemView.findViewById(R.id.RecommendedTeach);
            backgroundHeader = (View)itemView.findViewById(R.id.backgroundHeader);
            recommended1 = (ImageView)itemView.findViewById(R.id.Recommended1);
            recommended2 = (ImageView)itemView.findViewById(R.id.Recommended2);
            recommended3 = (ImageView)itemView.findViewById(R.id.Recommended3);
            recommended1Text = (TextView)itemView.findViewById(R.id.Recommended1Text);
            recommended2Text = (TextView)itemView.findViewById(R.id.Recommended2Text);
            recommended3Text = (TextView)itemView.findViewById(R.id.Recommended3Text);
            profilePicture = (ImageView)itemView.findViewById(R.id.profilePicture);
            scrollview = (ScrollView)itemView.findViewById(R.id.scrollview);
        }
    }


}
