package no.oslomet.meet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

        isNewUser = true; //getIntent().getBooleanExtra("NewUser", false);
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

    public DatePickerDialog setDate() { return new DatePickerDialog(ActivityMyProfile.this, setDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)); }
    public DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);
            String date = dayOfMonth + "-" + month + "-" + year;
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
                user.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

                learnDialougeItems = h._toArrayAdapter(ActivityMyProfile.this, notSelectedForLearning);
                teachDialougeItems = h._toArrayAdapter(ActivityMyProfile.this, notSelectedForTeaching);

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
    private void Retrieve_Hobbies()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                available_Hobbies = new JsonHandler().getHobbies(new Api().GET(Strings.Hobbies()));
            }
        });
    }
}
