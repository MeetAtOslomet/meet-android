package no.oslomet.meet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import no.oslomet.meet.Adapters.AdapterLanguage;
import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.ListViewExpander;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.Languages;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.User;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Strings;

public class ActivityMyProfile extends AppCompatActivity {

    public User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(1990, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog = setDate();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setListeners();
        setSpinners();
        getAllLanguages();
        getUser();
    }

    Calendar calendar;
    DatePickerDialog dialog;

    public DatePickerDialog setDate()
    {
        return new DatePickerDialog(ActivityMyProfile.this, setDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    private void setListeners()
    {
        ((EditText)findViewById(R.id.dateEdit)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !dialog.isShowing())
                    dialog.show();
            }
        });
        findViewById(R.id.dateEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing())
                    dialog.show();
            }
        });

    }

    public DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);

            String date = dayOfMonth + "-" + month + "-" + year;
            ((EditText)findViewById(R.id.dateEdit)).setText(date);
        }
    };

    Spinner genderSpinner;
    Spinner positionSpinner;
    private void setSpinners()
    {
        String[] genderArray = getResources().getStringArray(R.array.genders);
        genderSpinner = (Spinner)findViewById(R.id.spinnerGender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                genderArray
        );
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setGender(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String[] positionArray = getResources().getStringArray(R.array.positions);
        positionSpinner = (Spinner)findViewById(R.id.spinnerPosition);
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                positionArray
        );
        positionSpinner.setAdapter(positionAdapter);
        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                user.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    AdapterLanguage adapterLanguage;
    public void getAllLanguages()
    {
        adapterLanguage = new AdapterLanguage(this, new ArrayList<Languages>());
        ((ListView)findViewById(R.id.listView_LangImprove)).setAdapter(adapterLanguage);


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String response = new Api().GET(Strings.Languages());
                final ArrayList<Languages> lang = new JsonHandler().getLanguages(response);
                if (lang != null && lang.size() > 0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLanguageSelect(lang);
                            findViewById(R.id.addLanguageToLearn).setEnabled(true);
                        }
                    });
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.addLanguageToLearn).setEnabled(false);
                        }
                    });
                }
            }
        });


        //AdapterLanguage adapterLanguage = new AdapterLanguage(this)
    }

    public ArrayList<Languages> availableLanguages;
    private ListView langToImprove;

    private void setLanguageSelect(ArrayList<Languages> langs)
    {
        langToImprove = findViewById(R.id.listView_LangImprove);
        availableLanguages = langs;
        final AlertDialog.Builder languageSelect = new AlertDialog.Builder(ActivityMyProfile.this);
        final ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(ActivityMyProfile.this, android.R.layout.select_dialog_singlechoice);
        for (Languages lang : langs)
        {
            languageAdapter.add(lang.name);
        }
        languageSelect.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        languageSelect.setAdapter(languageAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("Selected", languageAdapter.getItem(i));
                if (availableLanguages != null)
                {
                    adapterLanguage.addIfNotPresent(availableLanguages.get(i));
                    ListViewExpander.setListViewHeightBasedOnChildren(langToImprove);
                }
            }
        });

        ((ImageButton)findViewById(R.id.addLanguageToLearn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSelect.show();
            }
        });
    }



    public void getUser()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String getReq = Strings.ApiUrl() + "?request=get_id_user&username=" +
                        new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_username) +
                        "&authenticationToken=" + new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey);
                Api api = new Api();
                String response = api.GET(getReq);
                int id_user = new JsonHandler().getIdUser(response);
                if (id_user != -1)
                {
                    String data = "";

                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("id_user", id_user);
                        jo.put("authenticationToken", new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey));
                        data = jo.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String getUser = Strings.ApiUrl() + "?request=get_me" +
                            "&authenticationToken=" + new SettingsHandler().getStringSetting(ActivityMyProfile.this, R.string.preference_AuthKey)+
                            "&data="+data;

                    String resp = api.GET(getUser);
                    final User user = new JsonHandler().getUser(resp);
                    System.out.print(user);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setUserFields(user);
                        }
                    });
                }
            }
        });
    }

    public void setUserFields(User user)
    {
        if (user == null)
        {
            return;
        }

        ((EditText)findViewById(R.id.inputFirstName)).setText(user.getFirstName());
        ((EditText)findViewById(R.id.inputLastName)).setText(user.getLastName());
        Date date = new Date(user.getAge());
        calendar.setTime(date);
        dialog = setDate();
        ((EditText)findViewById(R.id.dateEdit)).setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" +(calendar.get(Calendar.MONTH)+1) + "-" +calendar.get(Calendar.YEAR));
        if (user.getGender() > 0)
        {
            Log.e("VALUES SET", "Gender provieded: " + user.getGender());
            if (genderSpinner != null) {
                genderSpinner.setSelection(user.getGender());
            }
            //((Spinner)findViewById(R.id.spinnerGender)).setSelection(user.gender);
        }
        if (user.getType() > -1)
        {
            Log.e("VALUES SET", "Type provieded: " + user.getType());
            if (positionSpinner != null)
            {
                positionSpinner.setSelection(user.getType());
            }
            //((Spinner)findViewById(R.id.spinnerPosition)).setSelection(user.type);
        }

    }

}
