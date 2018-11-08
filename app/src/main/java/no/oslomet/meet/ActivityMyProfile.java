package no.oslomet.meet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import no.oslomet.meet.Adapters.AdapterHobby;
import no.oslomet.meet.Adapters.AdapterLanguage;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.ListViewExpander;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.Campus;
import no.oslomet.meet.classes.Hobbies;
import no.oslomet.meet.classes.Languages;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.User;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Helper;
import no.oslomet.meet.core.Strings;

public class ActivityMyProfile extends AppCompatActivity
{
    private int id_user = -1;
    public User user;
    boolean isNewUser = false;


    private Calendar calendar;
    private DatePickerDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(1990, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog = setDate();

        isNewUser = getIntent().getBooleanExtra("NewUser", false);
    }

    private AdapterHobby adapterHobby = new AdapterHobby(this, new ArrayList<Hobbies>());
    private ArrayAdapter<String> arrayHobby;

    private AdapterLanguage toLearn = new AdapterLanguage(this, new ArrayList<Languages>());
    private AdapterLanguage toTeach = new AdapterLanguage(this, new ArrayList<Languages>());
    private ArrayAdapter<String> teachDialougeItems;
    private ArrayAdapter<String> learnDialougeItems;


    @Override
    protected void onStart() {
        super.onStart();
        initSpinner();

        if (!isNewUser)
        {
            getIdUser();
            RetrieveUserData();
        }
        else
        {
            user = new User();
            Retrieve_Languages();
            Retrieve_Hobbies();
        }

        initListeners();
    }

    public void getIdUser()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String response = new Api().GET(Strings.Request_GetIdUser(new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_username), new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey)  ));
                id_user = new JsonHandler().getIdUser(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RetrieveUserData();
                    }
                });
            }
        });
    }

    /**
     * This method is for posting user when the user is not registered
     * The method will return an id for the user created
     * Required to run this method: RUN IN ASYNC!
     * @return int
     */
    private int postUserForId()
    {
        int out = 0;
        try {
            String jString = new JsonHandler()._toUserJSON(user);
            Api api = new Api();

            ArrayList<PostParam> pp = new ArrayList<>();
            pp.add(new PostParam("request", "add_user"));
            pp.add(new PostParam("authenticationToken", new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey)));
            pp.add(new PostParam("data", jString));

            String addResp = api.POST(Strings.ApiUrl(), api.POST_DATA(pp));
            ApiDataResponse adr = new JsonHandler().getData(addResp);
            if (adr.dataExit == 0)
            {
                String response = new Api().GET(Strings.Request_GetIdUser(new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_username), new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey)  ));
                int outId = new JsonHandler().getIdUser(response);
                if (outId < 0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityMyProfile.this, "Something went wrong when adding your profile, please try again later..", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return outId;

            }



        } catch (JSONException e) {
            e.printStackTrace();
            out = -1;
        }
        return out;
    }

    public DatePickerDialog setDate() { return new DatePickerDialog(ActivityMyProfile.this, setDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)); }
    public DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);
            String date = dayOfMonth + "-" + (month+1) + "-" + year;
            ((EditText)findViewById(R.id.dateEdit)).setText(date);
        }
    };

    private void initSpinner()
    {
        Spinner gender = ((Spinner)findViewById(R.id.spinnerGender));
        gender.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.genders)
        ));
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (user != null)
                    user.setGender(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        Spinner position = ((Spinner)findViewById(R.id.spinnerPosition));
        position.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.positions)
        ));
        position.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (user != null)
                    user.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String jString = new Api().GET(Strings.Campus());
                final ArrayList<Campus> campuses = new JsonHandler().getCampuses(jString);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> campusAdapter = new ArrayAdapter<String>(ActivityMyProfile.this, android.R.layout.simple_spinner_dropdown_item);
                        campusAdapter.add(getResources().getString(R.string.campusDefault));
                        for (Campus c : campuses)
                        {
                            campusAdapter.add(c.name);
                        }
                        Spinner campus = ((Spinner)findViewById(R.id.spinnerCampus));
                        campus.setAdapter(campusAdapter);

                        campus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (user != null)
                                    user.setCampus(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                });

            }
        });




    }

    private void initListeners()
    {
        EditText dateEdit = (EditText)findViewById(R.id.dateEdit);
        dateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !dialog.isShowing())
                    dialog.show();
            }
        });
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing())
                    dialog.show();
            }
        });

        findViewById(R.id.profileUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

    }

    private AlertDialog.Builder getBaseDialog(final ArrayAdapter<String> adapter, final AdapterLanguage lang, final int ListViewId)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(ActivityMyProfile.this);
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (adapter != null)
                {
                    String langName = adapter.getItem(which);
                    for (Languages l : available_Languages)
                    {
                        if (l.name.equals(langName))
                        {
                            adapter.remove(adapter.getItem(which));
                            lang.addIfNotPresent(l);
                            ListViewExpander.setListViewHeightBasedOnChildren( ((ListView)findViewById(ListViewId)) );
                        }
                    }

                }
            }
        });
        return adb;
    }



    private ArrayList<Languages> userLanguages = new ArrayList<>();
    private ArrayList<Hobbies> userHobbies = new ArrayList<>();
    private void RetrieveUserData()
    {
        if (id_user <= -1)
            return;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String token = new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey);
                ArrayList<PostParam> pp = new ArrayList<>();
                pp.add(new PostParam("authenticationToken", token));
                pp.add(new PostParam("id_user", String.valueOf(id_user)));
                String request = Strings.Request_GetMe(token, new JsonHandler()._toJson(pp));
                user = new JsonHandler().getUser(new Api().GET(request));

                userLanguages = new JsonHandler().getLanguages( new Api().GET(  Strings.Request_UserLanguages(String.valueOf(id_user)) ));
                userHobbies = new JsonHandler().getHobbies(new Api().GET( Strings.Request_UserHobbies(String.valueOf(id_user))));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null)
                            InsertUserData();
                    }
                });
            }
        });
    }

    private void InsertUserData()
    {
        ((EditText)findViewById(R.id.inputFirstName)).setText(user.getFirstName());
        ((EditText)findViewById(R.id.inputLastName)).setText(user.getLastName());
        Date date = new Date(user.getAge());
        calendar.setTime(date);
        dialog = setDate();
        ((EditText)findViewById(R.id.dateEdit)).setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" +(calendar.get(Calendar.MONTH)+1) + "-" +calendar.get(Calendar.YEAR));
        if (user.getGender() > 0)
            ((Spinner)findViewById(R.id.spinnerGender)).setSelection(user.getGender());
        if (user.getType() > -1)
            ((Spinner)findViewById(R.id.spinnerPosition)).setSelection(user.getType());

        Switch hideAge = (Switch)findViewById(R.id.switchShowAge);
        hideAge.setChecked(user.getHideAge() != 1);

        Retrieve_Languages();
        Retrieve_Hobbies();
    }

    ArrayList<Languages> available_Languages;
    private void Retrieve_Languages()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                available_Languages = new JsonHandler().getLanguages(new Api().GET(Strings.Languages()));
                Helper h = new Helper();
                ArrayList<Languages> notSelectedForLearning = h.extractAvailableForLearning(available_Languages, userLanguages);
                ArrayList<Languages> notSelectedForTeaching = h.extractAvailableForLearning(available_Languages, userLanguages);

                learnDialougeItems = h._toLanguageArrayAdapter(ActivityMyProfile.this, notSelectedForLearning);
                teachDialougeItems = h._toLanguageArrayAdapter(ActivityMyProfile.this, notSelectedForTeaching);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ListView)findViewById(R.id.listView_LangImprove)).setAdapter(toLearn);
                        ((ListView)findViewById(R.id.listView_LangTeach)).setAdapter(toTeach);

                        Helper h = new Helper();
                        toLearn.swapItems(h.getLearningLanguages(userLanguages));
                        ListViewExpander.setListViewHeightBasedOnChildren(((ListView)findViewById(R.id.listView_LangImprove)));
                        toTeach.swapItems(h.getTeachingLanguages(userLanguages));
                        ListViewExpander.setListViewHeightBasedOnChildren(((ListView)findViewById(R.id.listView_LangTeach)));

                        final AlertDialog.Builder learn = getBaseDialog(learnDialougeItems, toLearn, R.id.listView_LangImprove);
                        final AlertDialog.Builder teach = getBaseDialog(teachDialougeItems, toTeach, R.id.listView_LangTeach);

                        findViewById(R.id.addLanguageToLearn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (toLearn.getCount() >= 7)
                                    Toast.makeText(ActivityMyProfile.this, "You can only add up to 7 languages", Toast.LENGTH_LONG).show();
                                else
                                    learn.show();
                            }
                        });
                        findViewById(R.id.addLanguageToTeach).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (toTeach.getCount() >= 7)
                                    Toast.makeText(ActivityMyProfile.this, "You can only add up to 7 languages", Toast.LENGTH_LONG).show();
                                else
                                    teach.show();
                            }
                        });
                    }
                });

            }
        });
    }

    ArrayList<Hobbies> available_Hobbies;
    ArrayList<Hobbies> nonSelectedHobbies;
    private void Retrieve_Hobbies()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                available_Hobbies = new JsonHandler().getHobbies(new Api().GET(Strings.Hobbies()));
                nonSelectedHobbies = new Helper().extractAvailableHobbies(available_Hobbies, userHobbies);
                arrayHobby = new Helper()._toHobbyArrayAdapter(ActivityMyProfile.this, nonSelectedHobbies);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        adapterHobby.swapItems(userHobbies);
                        ((ListView)findViewById(R.id.listView_Hobbies)).setAdapter(adapterHobby);
                        ListViewExpander.setListViewHeightBasedOnChildren(((ListView)findViewById(R.id.listView_Hobbies)));


                        final AlertDialog.Builder adb = new AlertDialog.Builder(ActivityMyProfile.this);
                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.setAdapter(arrayHobby, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (arrayHobby != null)
                                {
                                    String langName = arrayHobby.getItem(which);
                                    for (Hobbies l : available_Hobbies)
                                    {
                                        if (l.getName().equals(langName))
                                        {
                                            arrayHobby.remove(arrayHobby.getItem(which));
                                            adapterHobby.addIfNotPresent(l);
                                            ListViewExpander.setListViewHeightBasedOnChildren( ((ListView)findViewById(R.id.listView_Hobbies)) );
                                        }
                                    }

                                }
                            }
                        });
                        findViewById(R.id.addHobbies).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adb.show();
                            }
                        });
                    }


                });

            }
        });
    }


    private String getHobbies()
    {
        String hobbies = "";
        Hobbies lastItem = (Hobbies) adapterHobby.getItem(adapterHobby.getItems().size()-1);
        for (Hobbies h : adapterHobby.getItems())
        {
            if (h == lastItem)
                hobbies += h.getIdHobby();
            else
                hobbies += h.getIdHobby() + ",";
        }

        ArrayList<PostParam> pp = new ArrayList<>();
        pp.add(new PostParam("id_user", String.valueOf(id_user)));
        pp.add(new PostParam("id_hobbies", hobbies));
        hobbies = new JsonHandler()._toJson(pp);

        return hobbies;
    }

    private String addLanguagesJSON()
    {
        String languages = null;
        ArrayList<Languages> merged = new Helper().mergeLanguages(toLearn.getItems(), toTeach.getItems(), id_user);

        try {
            languages = new JsonHandler()._toLanguageJsonArrayString("languages", merged);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return languages;
    }

    private void upload()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (isNewUser)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int genderPosition = ((Spinner)findViewById(R.id.spinnerGender)).getSelectedItemPosition();
                            int typePosition = ((Spinner)findViewById(R.id.spinnerPosition)).getSelectedItemPosition();
                            int campusPosition = ((Spinner)findViewById(R.id.spinnerCampus)).getSelectedItemPosition();
                            Log.e("Spinnser VAL", "Gender => " + genderPosition + " Type => " + typePosition + " Campus => " + campusPosition);
                            user = new User(
                                    0,
                                    new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_username),
                                    ((EditText)findViewById(R.id.inputFirstName)).getText().toString(),
                                    ((EditText)findViewById(R.id.inputLastName)).getText().toString(),
                                    calendar.getTimeInMillis(),
                                    typePosition,
                                    genderPosition,
                                    campusPosition,
                                    ""
                            );
                            try {
                                Log.e("POST USER", new JsonHandler()._toUserJSON(user));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    id_user = postUserForId();
                                    PerformUpload();
                                }
                            });
                        }
                    });


                }
                else
                {
                    PerformUpload();
                }



            }
        });



    }


    private void PerformUpload()
    {
        if (id_user > 0)
        {
            Api api = new Api();
            ArrayList<PostParam> pp = new ArrayList<>();
            pp.add(new PostParam("authenticationToken", new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey)));

            ArrayList<PostParam> langPost = new ArrayList<>(pp);
            langPost.add(new PostParam("request", "add_language"));
            langPost.add(new PostParam("data",addLanguagesJSON() ));
            String languages = addLanguagesJSON();
            Log.e("JSON POST Lang", languages);
            String langresponse = api.POST(Strings.ApiUrl(), api.POST_DATA(langPost));
            Log.e("API POST RESPONSE", langresponse);

            ApiDataResponse langaugeResponse = new JsonHandler().getData(langresponse);

            ArrayList<PostParam> hobbyPost = new ArrayList<>(pp);
            hobbyPost.add(new PostParam("request", "add_hobbies"));
            hobbyPost.add(new PostParam("data", getHobbies()));
            Log.e("JSON POST Hobby",  getHobbies());
            String hobbyResponse = api.POST(Strings.ApiUrl(), api.POST_DATA(hobbyPost));
            Log.e("API POST RESPONSE", hobbyResponse);

            ApiDataResponse hobbiesResponse = new JsonHandler().getData(hobbyResponse);

            if (isNewUser)
            {
                if (langaugeResponse.dataExit == 0 && hobbiesResponse.dataExit == 0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ActivityMyProfile.this, ActivityMain.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }

        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder adb = new AlertDialog.Builder(ActivityMyProfile.this);
                    adb.setTitle("Error");
                    adb.setMessage("And error occurred when trying to get your user id, this prevents us from posting your data..");
                    adb.show();
                }
            });
        }
    }
}
