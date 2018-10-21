package no.oslomet.meet;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.AuthStatus;
import no.oslomet.meet.classes.Heartbeat;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.Registration;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.Strings;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (no.oslomet.meet.core.Certificate.sslContext == null)
        {
            no.oslomet.meet.core.Certificate cert = new no.oslomet.meet.core.Certificate();
            cert.InitializeCetificate(this);
        }
    }

    //https://developer.android.com/training/articles/security-ssl#java

    ImageView mainLogo;
    ProgressBar spinner;
    protected void onResume()
    {
        super.onResume();
        mainLogo = findViewById(R.id.logo);
        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run()
            {
                Api api = new Api();
                final String response = api.GET(Strings.Heartbeat());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Heartbeat h = new JsonHandler().getHeartbeat(response);
                        if (h != null && h.Status == true)
                        {

                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mainLogo.getLayoutParams();
                            TranslateAnimation animation = new TranslateAnimation(0,0, 0, -lp.topMargin);
                            animation.setDuration(1000);
                            animation.setFillAfter(false);
                            animation.setAnimationListener(new SplashAnimationListener());
                            mainLogo.startAnimation(animation);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    ValidateAuthentication();
                                }
                            });
                        }



                        TextView tv = MainActivity.this.findViewById(R.id.HeartBeat);
                        tv.append(response);
                    }
                });



                /*ArrayList<PostParam> pptest = new ArrayList<>();
                pptest.add(new PostParam("request", "register_user"));
                pptest.add(new PostParam("data", Strings.testPost));


                String data = api.POST_DATA(pptest);
                final String resp = api.POST(Strings.ApiUrl(), data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = MainActivity.this.findViewById(R.id.HeartBeat);
                        tv.append(resp);
                    }
                });*/
            }
        });
    }

    private void ValidateAuthentication()
    {
        Api api = new Api();
        String AuthKey = new SettingsHandler().getStringSetting(MainActivity.this, R.string.preference_AuthKey);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
            }
        });
        if (AuthKey == null || AuthKey.length() == 0)
        {
            //Present login or register
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showUserInput();
                }
            });
        }
        else
        {
            //Continue
            ArrayList<PostParam> reqs = new ArrayList<>();
            reqs.add(new PostParam("request", "auth_check"));
            reqs.add(new PostParam("authenticationToken", AuthKey));
            Log.e("AuthKey-Test", AuthKey);
            final String response2 = api.POST(Strings.ApiUrl(), api.POST_DATA(reqs));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AuthStatus authStatus = new JsonHandler().getHasAuth(response2);
                    if (authStatus == null || authStatus.authenticationExit > 0)
                    {
                        showUserInput();
                    }
                    else
                    {
                        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                        adb.setTitle("Success");
                        adb.setMessage("You where successfully authenticated!");
                        adb.show();
                        //Authorize connection
                    }
                }
            });
        }
    }


    private void showUserInput()
    {
        String username = new SettingsHandler().getStringSetting(MainActivity.this, R.string.preference_username);
        Boolean awaitingValidation = new SettingsHandler().getBooleanSetting(MainActivity.this, R.string.preference_validate, false);

        if (username != null && username != "" && awaitingValidation == true )
        {
            //Show authentication code
            HandleActivation();

        }
        else
        {
            findViewById(R.id.selectLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.PromptloginButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.selectLayout).setVisibility(View.GONE);
                    findViewById(R.id.loginLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Login();
                        }
                    });
                }
            });

            findViewById(R.id.PromptregisterButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.registerUserLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.registerUsernameLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.registerUsernameButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            final String username = ((EditText)findViewById(R.id.registerUsernameInput)).getText().toString();
                            if (username.contains("@"))
                            {
                                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                                adb.setTitle("Illegal character!");
                                adb.setMessage("Only insert your username (student number or employee number). Example sXXXXX or olanordmann");
                                adb.show();
                            }
                            else
                            {
                                final JSONObject root = new JSONObject();
                                try {
                                    root.put("username", username);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        String json = root.toString();
                                        ArrayList<PostParam> params = new ArrayList<>();
                                        params.add(new PostParam("request", "register_user"));
                                        params.add(new PostParam("data", json));

                                        Api api = new Api();
                                        String reuslt = api.POST(Strings.ApiUrl(), api.POST_DATA(params));
                                        Registration registration = new JsonHandler().getRegistration(reuslt);
                                        if (registration != null && registration.registrationExit == 0)
                                        {
                                            //Store username
                                            new SettingsHandler().setStringSetting(MainActivity.this, R.string.preference_username, username);
                                            new SettingsHandler().setBooleanSetting(MainActivity.this, R.string.preference_validate, true);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    findViewById(R.id.registerUsernameLayout).setVisibility(View.GONE);
                                                    HandleActivation();
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                        }
                    });
                }
            });
        }
    }


    private void HandleActivation()
    {
        findViewById(R.id.registerUserLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.registerUserCode).setVisibility(View.VISIBLE);

        final EditText code1 = (EditText)findViewById(R.id.code1);
        final EditText code2 = (EditText)findViewById(R.id.code2);
        final EditText code3 = (EditText)findViewById(R.id.code3);
        final EditText code4 = (EditText)findViewById(R.id.code4);

        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    findViewById(R.id.code2).requestFocus();
            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    findViewById(R.id.code3).requestFocus();
            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    findViewById(R.id.code4).requestFocus();
            }
        });

        code4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL)
                {
                    EditText et = ((EditText)v);
                    if (et.getText().length() == 0) { code3.requestFocus(); }
                }
                return false;
            }
        });

        code3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL)
                {
                    EditText et = ((EditText)v);
                    if (et.getText().length() == 0) { code2.requestFocus(); }
                }
                return false;
            }
        });

        code2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL)
                {
                    EditText et = ((EditText)v);
                    if (et.getText().length() == 0) { code1.requestFocus(); }
                }
                return false;
            }
        });


        findViewById(R.id.UserCodeConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code =
                        ((EditText)findViewById(R.id.code1)).getText().toString() +
                                ((EditText)findViewById(R.id.code2)).getText().toString() +
                                ((EditText)findViewById(R.id.code3)).getText().toString() +
                                ((EditText)findViewById(R.id.code4)).getText().toString();




                Toast.makeText(MainActivity.this, code, Toast.LENGTH_LONG).show();
            }
        });

        //UserCodeConfirm
    }

    private void Login()
    {
        String username = ((EditText)findViewById(R.id.loginUser)).getText().toString();
        String password = ((EditText)findViewById(R.id.loginPass)).getText().toString();

        JSONObject root = new JSONObject();
        try {
            root.put("username", username);
            root.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = root.toString();
        final ArrayList<PostParam> params = new ArrayList<>();
        params.add(new PostParam("request","login_user"));
        params.add(new PostParam("data", json));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Api api = new Api();
                String result = api.POST(Strings.ApiUrl(), api.POST_DATA(params));
                AuthStatus as = new JsonHandler().getHasAuth(result);
                if (as != null && as.authenticationExit == 0 && as.authenticationToken.length() > 0)
                {
                    new SettingsHandler().setStringSetting(MainActivity.this, R.string.preference_AuthKey, as.authenticationToken);
                    Log.e("AuthKey", as.authenticationToken);
                    ValidateAuthentication();
                }
            }
        });

    }



    public class SplashAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mainLogo.clearAnimation();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)mainLogo.getLayoutParams();
            lp.setMargins(0, 0, 0, lp.topMargin);
            mainLogo.setLayoutParams(lp);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
