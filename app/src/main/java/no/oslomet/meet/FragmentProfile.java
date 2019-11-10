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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.Hobbies;
import no.oslomet.meet.classes.Languages;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.User;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Helper;
import no.oslomet.meet.core.Strings;

import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;


public class FragmentProfile extends Fragment {

    private ArrayList<Languages> userLanguages = new ArrayList<>();
    private ArrayList<Hobbies> userHobbies = new ArrayList<>();
    private int id_user = -1;
    public User user;

    ImageView learn1;
    ImageView learn2;
    ImageView learn3;
    TextView learn1Text;
    TextView learn2Text;
    TextView learn3Text;
    TextView position;
    TextView nameAndAge;
    TextView speaks;
    TextView hobbies;

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

        learn1 = (ImageView) getView().findViewById(R.id.learn1);
        learn2 = (ImageView) getView().findViewById(R.id.learn2);
        learn3 = (ImageView) getView().findViewById(R.id.learn3);
        learn1Text = (TextView) getView().findViewById(R.id.learn1Text);
        learn2Text = (TextView) getView().findViewById(R.id.learn2Text);
        learn3Text = (TextView) getView().findViewById(R.id.learn3Text);
        position = (TextView) getView().findViewById(R.id.position);
        nameAndAge = (TextView) getView().findViewById(R.id.nameAndAge);
        speaks = (TextView) getView().findViewById(R.id.speaks);
        hobbies = (TextView) getView().findViewById(R.id.hobbies);

        getView().findViewById(R.id.editSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SettingBottomSheetDialog settingsDialog = new SettingBottomSheetDialog();
                settingsDialog.show(getFragmentManager(), "SettingsBottomSheet");
               /*Intent intent = new Intent(getActivity(), ActivitySettings.class);
               startActivity(intent);*/
            }
        });


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
        getIdUser();
        RetrieveUserData();
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

    public void getIdUser() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String response = new Api().GET(Strings.Request_GetIdUser(new SettingsHandler().getStringSetting(getContext(), R.string.preference_username), new SettingsHandler().getStringSetting(getContext(), R.string.preference_AuthKey)));
                setId_user(new JsonHandler().getIdUser(response));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RetrieveUserData();
                    }
                });
            }
        });
    }

    private void setId_user(int id) {
        new SettingsHandler().setStringSetting(getContext(), R.string.preference_idUser, String.valueOf(id));
        id_user = id;
    }

    private void RetrieveUserData() {
        if (id_user <= -1)
            return;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String token = new SettingsHandler().getStringSetting(getContext(), R.string.preference_AuthKey);
                ArrayList<PostParam> pp = new ArrayList<>();
                pp.add(new PostParam("authenticationToken", token));
                pp.add(new PostParam("id_user", String.valueOf(id_user)));
                String request = Strings.Request_GetMe(token, new JsonHandler()._toJson(pp));
                user = new JsonHandler().getUser(new Api().GET(request));

                userLanguages = new JsonHandler().getLanguages(new Api().GET(Strings.Request_UserLanguages(String.valueOf(id_user))));
                userHobbies = new JsonHandler().getHobbies(new Api().GET(Strings.Request_UserHobbies(String.valueOf(id_user))));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) Log.d("UserProfile", "Data: " + user.getFirstName());
                        InsertUserData();
                    }
                });
            }
        });
    }

    private void InsertUserData() {
        Helper helper = new Helper();
        ArrayList<Languages> learn = helper.getLearningLanguages(userLanguages);
        ArrayList<Languages> teach = helper.getTeachingLanguages(userLanguages);

        String learnString = "";
        String teachString = "";
        String hobbiesString = "";
        for (Languages lang : learn) {
            if (lang.name == learn.get(learn.size() - 1).name) {
                learnString += lang.name;
            } else {
                learnString += lang.name + ", ";
            }
        }
        for (Languages lang : teach) {
            if (lang.name == teach.get(teach.size() - 1).name) {
                teachString += lang.name;
            } else {
                teachString += lang.name + ", ";
            }
        }
        for (Hobbies h : userHobbies) {
            if (h.getName() == userHobbies.get(userHobbies.size() - 1).getName()) {
                hobbiesString += h.getName();
            } else {
                hobbiesString += h.getName() + ", ";
            }
        }
        Context c = getContext();
        if (c != null) {
            String[] positions = c.getResources().getStringArray(R.array.positions);
            String[] campuses = c.getResources().getStringArray(R.array.campuses);

            String displayPosition = "";
            displayPosition += positions[user.getType()] + " at " + campuses[user.getIdCampus() - 1];
            position.setText(displayPosition);

            nameAndAge.setText(user.getFirstName() + " " + user.getLastName() + ", " + user.getReadableAge());
            speaks.setText(teachString);
            hobbies.setText(hobbiesString);
            setCountries(learn);

        }

    }

    /**
     * @param learn is an ArrayList<Languages>
     *              Displays the languages and their country codes as flags on the matching card.
     *              Will check each language up to a match in a predefined String array in Strings.xml
     *              Only converts the first three languages of the language list.
     */
    public void setCountries(ArrayList<Languages> learn) {
        //TODO: CLEAN UP THIS MESSY CODE. INEFFECTIVE CODE.

        String[] languagesXML = getResources().getStringArray(R.array.languages_array);
        String[] codesXML = getResources().getStringArray(R.array.countryCode_array);


        String code1 = (learn.size() > 0 ? (getCountryCode(learn.get(0), languagesXML, codesXML)) : (null));
        String code2 = (learn.size() > 1 ? (getCountryCode(learn.get(1), languagesXML, codesXML)) : (null));
        String code3 = (learn.size() > 2 ? (getCountryCode(learn.get(2), languagesXML, codesXML)) : (null));

        if (code1 != null) {
            Picasso.get().load("https://www.countryflags.io/" + code1 + "/flat/64.png").into(learn1);
            learn1Text.setText(learn.get(0).name);
        }

        if (code2 != null) {
            Picasso.get().load("https://www.countryflags.io/" + code2 + "/flat/64.png").into(learn2);
            learn2Text.setText(learn.get(1).name);
        }

        if (code3 != null) {
            Picasso.get().load("https://www.countryflags.io/" + code3 + "/flat/64.png").into(learn3);
            learn3Text.setText(learn.get(2).name);
        }
    }

    public String getCountryCode(Languages l, String[] langs, String[] codes) {


        int counter = 0;
        for (String s : langs) {
            if (l.name.equals(s)) {
                return codes[counter];
            }
            counter++;
        }
        return null;
    }
}
