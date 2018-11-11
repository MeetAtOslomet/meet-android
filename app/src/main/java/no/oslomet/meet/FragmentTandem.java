package no.oslomet.meet;

import android.content.Context;
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

import no.oslomet.meet.Handler.SettingsHandler;
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
        loadRecommendedTandems();
    }


    private void loadRecommendedTandems()
    {
        RecyclerView rv = (RecyclerView)getView().findViewById(R.id.tandemSuggestedRecycler);
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Api api = new Api();
                String response = api.GET(Strings.Request_Get_MY_TANDEM(new SettingsHandler().getStringSetting(getActivity(), R.string.preference_idUser)));
                Log.e("Fetched TANDEM", response);
            }
        });
    }


}
