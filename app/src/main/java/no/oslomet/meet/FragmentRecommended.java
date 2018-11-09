package no.oslomet.meet;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONException;

import java.util.ArrayList;

import no.oslomet.meet.Adapters.AdapterRecommended;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.Recommended;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Strings;


public class FragmentRecommended extends Fragment {

    public FragmentRecommended() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommended, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GetRecommended();
    }

    private void GetRecommended()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Api api = new Api();
                String response = api.GET(Strings.Request_GetIdUser(new SettingsHandler().getStringSetting(getActivity(), R.string.preference_username), new SettingsHandler().getStringSetting(getActivity(), R.string.preference_AuthKey)  ));
                int id_user = new JsonHandler().getIdUser(response);

                ArrayList<PostParam> jsonParam = new ArrayList<>();
                jsonParam.add(new PostParam("id_user", String.valueOf(id_user)));
                jsonParam.add(new PostParam("username", new SettingsHandler().getStringSetting(getActivity(), R.string.preference_username)));

                String data = new JsonHandler()._toJson(jsonParam);
                ArrayList<PostParam> pp = new ArrayList<>();
                pp.add(new PostParam("request", "get_recommended"));
                pp.add(new PostParam("authenticationToken", new SettingsHandler().getStringSetting(getActivity(), R.string.preference_AuthKey)));
                pp.add(new PostParam("data", data));

                String getParam = api.POST_DATA(pp);
                String urlWithArgs = Strings.ApiUrl() + "?" + getParam;
                Log.e("GET URL", urlWithArgs);

                String out = api.GET(urlWithArgs);
                Log.e("API OUT", "=>" + out);
                final ArrayList<Recommended> recs;
                try {
                    recs = new JsonHandler().getRecommended(out);
                    Log.e("Recommended", recs.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setStack(recs);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    AdapterRecommended recommendedAdapter;
    public void setStack(ArrayList<Recommended> items)
    {
        RecyclerView RecyclerView_Recommended = (RecyclerView)getView().findViewById(R.id.RecyclerView_Recommended);
        //RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams)RecyclerView_Recommended.getLayoutParams();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView_Recommended.setLayoutManager(llm);

        recommendedAdapter = new AdapterRecommended(getActivity(), items);
        RecyclerView_Recommended.setAdapter(recommendedAdapter);
        RecyclerView_Recommended.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return true;
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });

        enableButtons();
    }

    private void enableButtons()
    {
        getView().findViewById(R.id.Recommended_Check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getView().findViewById(R.id.Recommended_Decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recommendedAdapter.getItemCount() > 0)
                    recommendedAdapter.removeItem(0);
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
