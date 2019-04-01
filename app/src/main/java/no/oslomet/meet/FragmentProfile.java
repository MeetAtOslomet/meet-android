package no.oslomet.meet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Strings;


public class FragmentProfile extends Fragment {


    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        getView().findViewById(R.id.editProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), ActivityMyProfile.class));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        


        getView().findViewById(R.id.signOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Api api = new Api();

                        ArrayList<PostParam> pp = new ArrayList<>();
                        pp.add(new PostParam("id_user", new SettingsHandler().getStringSetting(getContext(), R.string.preference_idUser)));
                        pp.add(new PostParam("username", new SettingsHandler().getStringSetting(getContext(), R.string.preference_username)));
                        pp.add(new PostParam("authenticationToken", new SettingsHandler().getStringSetting(getContext(), R.string.preference_AuthKey)));

                        String jsonData = new JsonHandler()._toJson(pp);
                        String response = api.GET(Strings.ApiUrl() + "?request=sign_out&data="+jsonData);
                        Log.e("Sign out", response);
                        ApiDataResponse adr = new JsonHandler().getData(response);
                        if (adr != null && adr.dataExit == 0)
                        {
                            SettingsHandler sh = new SettingsHandler();
                            sh.setStringSetting(getContext(), R.string.preference_idUser, "");
                            sh.setStringSetting(getContext(), R.string.preference_username, "");
                            sh.setStringSetting(getContext(), R.string.preference_AuthKey, "");

                            Intent i = new Intent(getActivity(), ActivityLaunch.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            //Exit to launcher
                        }

                    }
                });



            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
