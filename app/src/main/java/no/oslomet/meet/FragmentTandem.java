package no.oslomet.meet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;

import no.oslomet.meet.Adapters.AdapterRequests;
import no.oslomet.meet.Adapters.AdapterTandem;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.Requests;
import no.oslomet.meet.classes.Tandem;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Strings;

public class FragmentTandem extends Fragment {

    public FragmentTandem() {
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
        return inflater.inflate(R.layout.fragment_tandem, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadTandems();
        loadRecommendedTandems();
        getView().findViewById(R.id.planningFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), ActivityCalendar.class));
            }
        });
    }


    private void loadRecommendedTandems()
    {
        final RecyclerView rv = (RecyclerView)getView().findViewById(R.id.tandemSuggestedRecycler);
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Api api = new Api();
                String response = api.GET(Strings.Request_GetRequests(new SettingsHandler().getStringSetting(getContext(), R.string.preference_idUser)));
                try {
                    final ArrayList<Requests> requests = new JsonHandler().getRequests(response);
                    if (getActivity() instanceof ActivityMain)
                    {
                        ActivityMain activityMain = (ActivityMain) getActivity();
                        activityMain.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AdapterRequests adapterRequests = new AdapterRequests(getContext(), requests);
                                rv.setAdapter(adapterRequests);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTandems()
    {
        final ListView lv = (ListView)getView().findViewById(R.id.tandemMatchedList);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Api api = new Api();
                String response = api.GET(Strings.Request_Get_TANDEM(new SettingsHandler().getStringSetting(getContext(), R.string.preference_idUser)));
                Log.e("Fetched TANDEM", response);
                try {
                    final ArrayList<Tandem> tandems = new JsonHandler().getTandems(response);
                    if (getActivity() instanceof ActivityMain)
                    {
                        ActivityMain activityMain = (ActivityMain) getActivity();
                        activityMain.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AdapterTandem adapterTandem = new AdapterTandem(getContext(), tandems);
                                lv.setAdapter(adapterTandem);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterTandem at = (AdapterTandem) lv.getAdapter();
                Tandem t = (Tandem) at.getItem(position);
                if (t != null)
                {
                    Intent chatIntent = new Intent(getActivity(), ActivityChat.class);
                    chatIntent.putExtra("Tandem", t);
                    startActivity(chatIntent);
                }
            }
        });*/
    }


}
