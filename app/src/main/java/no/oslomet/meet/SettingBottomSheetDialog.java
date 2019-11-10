package no.oslomet.meet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Strings;

public class SettingBottomSheetDialog extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomaction_settings, container, false);

        TextView txtTerms = v.findViewById(R.id.infoTxtTerms);
        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsIntent= new Intent(getActivity(), ActivityTerms.class);
                getActivity().startActivity(termsIntent);
            }
        });
        //txtTerms.setMovementMethod(LinkMovementMethod.getInstance());

        TextView txtPrivacy = v.findViewById(R.id.infoTxtPrivacy);
        txtPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent privacyIntent= new Intent(getActivity(),ActivityPrivacy.class);
                getActivity().startActivity(privacyIntent);
            }
        });

        TextView txtLogout = v.findViewById(R.id.infoTxtLogout);
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Api api = new Api();

                        ArrayList<PostParam> pp = new ArrayList<>();
                        pp.add(new PostParam("id_user", new SettingsHandler().getStringSetting(getContext(), R.string.preference_idUser)));
                        pp.add(new PostParam("username", new SettingsHandler().getStringSetting(getContext(), R.string.preference_username)));
                        pp.add(new PostParam("authenticationToken", new SettingsHandler().getStringSetting(getContext(), R.string.preference_AuthKey)));

                        String jsonData = new JsonHandler()._toJson(pp);
                        String response = api.GET(Strings.ApiUrl() + "?request=sign_out&data=" + jsonData);
                        Log.e("Sign out", response);
                        ApiDataResponse adr = new JsonHandler().getData(response);
                        if (adr != null && adr.dataExit == 0) {
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


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
