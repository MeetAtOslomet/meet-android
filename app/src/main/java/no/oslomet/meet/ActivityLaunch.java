package no.oslomet.meet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.oslomet.meet.Handler.JsonHandler;
import no.oslomet.meet.Handler.SettingsHandler;
import no.oslomet.meet.classes.ApiDataResponse;
import no.oslomet.meet.classes.AuthStatus;
import no.oslomet.meet.classes.Heartbeat;
import no.oslomet.meet.classes.PostParam;
import no.oslomet.meet.classes.Registration;
import no.oslomet.meet.core.Api;
import no.oslomet.meet.core.FCM;
import no.oslomet.meet.core.Strings;

public class ActivityLaunch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        /**
         * Initiate our official signed key
         * Proceeds to add it to the android allowed key chain
         * (Root provider not present in android key chain)
         *
         * Key-file has to be updated before certificate expires
         */
        if (no.oslomet.meet.core.Certificate.sslContext == null) {
            no.oslomet.meet.core.Certificate cert = new no.oslomet.meet.core.Certificate();
            cert.InitializeCetificate(this);
        }

        /**
         * Initiates AppCenter SDK
         * Tells the service to listen for:
         * Analytics (Event defined in the App),
         * Crashes (Sends the app crashes to AppCenter along with diagnostic)
         */
        AppCenter.start(getApplication(), "fea3ea9e-e705-4e2e-bb14-62f0c667a5e5",
                Analytics.class, Crashes.class);

        createNotificationChannel();
    }

    /**
     * Enables notifications for android versions greater than Oreo 8.0 through a notifications channel.
     * Called by onCreate.
     */
    private void createNotificationChannel() {
        // If the version of android running on this hardware is equal or greater than Android Oreo (8.0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel def = new NotificationChannel(Strings.DEFAULT_CHANNEL_ID, "Default", NotificationManager.IMPORTANCE_DEFAULT);
            def.enableVibration(false);

            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            List<NotificationChannel> lnc = new ArrayList<>(Arrays.asList(
                    def
            ));
            nm.createNotificationChannels(lnc);
        }
    }

    public String getMetaString(String key) {
        try {
            ApplicationInfo appinfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            if (appinfo != null) {
                return appinfo.metaData.get(key).toString();
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    //https://developer.android.com/training/articles/security-ssl#java

    ImageView mainLogo;
    ProgressBar spinner;

    protected void onResume() {
        super.onResume();
        mainLogo = findViewById(R.id.logo);
        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create new API object
                Api api = new Api();

                // Use the API to GET Heartbeat and save it in a string
                final String response = api.GET(Strings.Heartbeat());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Create a new Heartbeat object from the response string
                        Heartbeat h = new JsonHandler().getHeartbeat(response);

                        // If you managed to create a Heartbeat object and it has a status of true
                        if (h != null && h.Status) {
                            // Animate the logo upwards while loading in the next layout
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mainLogo.getLayoutParams();
                            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -lp.topMargin);
                            animation.setDuration(1000);
                            animation.setFillAfter(false);
                            animation.setAnimationListener(new SplashAnimationListener());
                            mainLogo.startAnimation(animation);

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    // Continue by calling
                                    ValidateAuthentication();
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    /**
     * If user has log in data on their phone, try to authenticate that data with the server.
     * If data does not exist or is incorrect, show the user log in fields.
     * If user is authenticated call @method checkIfUserExists.
     * Must be ran on a non-main thread. Creates new thread if ran on main.
     */
    private void ValidateAuthentication() {
        // Check what thread you are on to make sure this method can not be run on any thread except main
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e("Mistake were made", "You where just about to run network code on main thread! This is not allowed by the Android OS!");
            // If you are running on the main thread, create a new AsyncTask and call this method again
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    ValidateAuthentication();
                    return;
                }
            });
        }

        // Create new API object
        Api api = new Api();

        // Check if the user is already logged in
        String AuthKey = new SettingsHandler().getStringSetting(ActivityLaunch.this, R.string.preference_AuthKey);

        // Remove the spinner from view
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
            }
        });

        // If user is not logged in, show them the user log in fields
        if (AuthKey == null || AuthKey.length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO
                    showUserInput();
                }
            });
        }
        // If user is logged in (an AuthKey exists), continue here
        else {
            // Get the username from settings
            username = new SettingsHandler().getStringSetting(ActivityLaunch.this, R.string.preference_username);

            // Create a new arraylist of arguments
            ArrayList<PostParam> reqs = new ArrayList<>();

            // First node is the request type for the API
            reqs.add(new PostParam("request", "auth_check"));

            // Second node is the Authentication Token
            reqs.add(new PostParam("authenticationToken", AuthKey));
            Log.e("AuthKey-Test", AuthKey);

            // Strings.ApiUrl() returns the url to the API. Transform your PostParam into a String list then Post and save the response
            final String response2 = api.POST(Strings.ApiUrl(), api.POST_DATA(reqs));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AuthStatus authStatus = new JsonHandler().getHasAuth(response2);
                    if (authStatus == null || authStatus.authenticationExit > 0) {
                        // TODO
                        showUserInput();
                    } else {
                        /*AlertDialog.Builder adb = new AlertDialog.Builder(ActivityLaunch.this);
                        adb.setTitle("Success");
                        adb.setMessage("You where successfully authenticated!");
                        adb.show();*/
                        Toast.makeText(ActivityLaunch.this, "Authenticated", Toast.LENGTH_SHORT).show();
                        //Authorize connection
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                checkIfUserExists();
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     *
     */
    private void checkIfUserExists() {
        Api api = new Api();
        String resp = api.GET(Strings.ProfileExists(username));
        ApiDataResponse adr = new JsonHandler().getData(resp);

        FCM fcm = new FCM();
        String AuthKey = new SettingsHandler().getStringSetting(ActivityLaunch.this, R.string.preference_AuthKey);
        fcm.GetToken(username, AuthKey);

        if (adr != null && adr.dataExit == 0) {
            String response = new Api().GET(Strings.Request_GetIdUser(new SettingsHandler().getStringSetting(ActivityLaunch.this, R.string.preference_username), new SettingsHandler().getStringSetting(ActivityLaunch.this, R.string.preference_AuthKey)));
            final int userID = new JsonHandler().getIdUser(response);
            if (userID > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new SettingsHandler().setStringSetting(ActivityLaunch.this, R.string.preference_idUser, String.valueOf(userID));
                        startActivity(new Intent(ActivityLaunch.this, ActivityMain.class));
                    }
                });
            }


            //Navigate to matching activity

            //startActivity(new Intent(ActivityLaunch.this, ActivityMatch.class));
        } else if (adr != null && adr.dataExit == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ActivityLaunch.this, ActivityMyProfile.class);
                    intent.putExtra("NewUser", true);
                    startActivity(intent);
                }
            });
        }

    }


    private String username = null;

    public void updateUsername(String username) {
        this.username = username;
    }

    private void showUserInput() {
        username = new SettingsHandler().getStringSetting(ActivityLaunch.this, R.string.preference_username);
        Boolean awaitingValidation = new SettingsHandler().getBooleanSetting(ActivityLaunch.this, R.string.preference_validate, false);
        Boolean awaitingPassword = new SettingsHandler().getBooleanSetting(ActivityLaunch.this, R.string.preference_setPassword, false);
        if (username != null && username != "" && awaitingValidation == true) {
            //Show authentication code
            HandleActivation();

        } else if (username != null && username != "" && awaitingPassword == true) {
            HandlePasswordSetting();
        } else {
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
                    findViewById(R.id.loginGoBackButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            findViewById(R.id.selectLayout).setVisibility(View.VISIBLE);
                            findViewById(R.id.loginLayout).setVisibility(View.GONE);
                        }
                    });
                }
            });

            findViewById(R.id.PromptregisterButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.selectLayout).setVisibility(View.GONE);
                    findViewById(R.id.registerUserLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.registerUsernameLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.registerUsernameButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RegisterUsername();
                        }
                    });
                    findViewById(R.id.txtRegisterTerms).setVisibility(View.VISIBLE);
                    findViewById(R.id.registerGoBackButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            findViewById(R.id.selectLayout).setVisibility(View.VISIBLE);
                            findViewById(R.id.registerUserLayout).setVisibility(View.GONE);
                            findViewById(R.id.registerUsernameLayout).setVisibility(View.GONE);
                            findViewById(R.id.txtRegisterTerms).setVisibility(View.GONE);
                        }
                    });

                    loadLegalText();

                }
            });
        }
    }
    /*Assigning html text to textView for consent text on registration page.*/
    private void loadLegalText()
    {
        String text = getString(R.string.legalNote);
        Spanned policy = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);
        TextView tv = findViewById(R.id.txtRegisterTerms);
        tv.setText(policy);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void RegisterUsername() {
        final String username = ((EditText) findViewById(R.id.registerUsernameInput)).getText().toString().trim();
        if (username.contains("@")) {
            AlertDialog.Builder adb = new AlertDialog.Builder(ActivityLaunch.this);
            adb.setTitle("Illegal character!");
            adb.setMessage("Only insert your username (student number or employee number). Example sXXXXX or olanordmann");
            adb.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            adb.show();
        } else {
            final JSONObject root = new JSONObject();
            try {
                root.put("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String json = root.toString();
                    ArrayList<PostParam> params = new ArrayList<>();
                    params.add(new PostParam("request", "register_user"));
                    params.add(new PostParam("data", json));

                    Api api = new Api();
                    String reuslt = api.POST(Strings.ApiUrl(), api.POST_DATA(params));
                    Registration registration = new JsonHandler().getRegistration(reuslt);
                    if (registration != null && registration.registrationExit == 0) {
                        //Store username
                        new SettingsHandler().setStringSetting(ActivityLaunch.this, R.string.preference_username, username);
                        updateUsername(username);
                        new SettingsHandler().setBooleanSetting(ActivityLaunch.this, R.string.preference_validate, true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.registerUsernameLayout).setVisibility(View.GONE);
                                HandleActivation();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Something went wrong with Registration, please contact our helpdesk.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }


    private void HandleActivation() {
        findViewById(R.id.registerUserLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.registerUserCode).setVisibility(View.VISIBLE);

        final EditText code1 = (EditText) findViewById(R.id.code1);
        final EditText code2 = (EditText) findViewById(R.id.code2);
        final EditText code3 = (EditText) findViewById(R.id.code3);
        final EditText code4 = (EditText) findViewById(R.id.code4);

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
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    EditText et = ((EditText) v);
                    if (et.getText().length() == 0) {
                        code3.requestFocus();
                    }
                }
                return false;
            }
        });
        code3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    EditText et = ((EditText) v);
                    if (et.getText().length() == 0) {
                        code2.requestFocus();
                    }
                }
                return false;
            }
        });
        code2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    EditText et = ((EditText) v);
                    if (et.getText().length() == 0) {
                        code1.requestFocus();
                    }
                }
                return false;
            }
        });


        Button confirmCode = (Button) findViewById(R.id.UserCodeConfirm);
        confirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString();
                final int activationCode = Integer.valueOf(code);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Api api = new Api();
                        ArrayList<PostParam> params = new ArrayList<>();
                        params.add(new PostParam("request", "activate_user"));

                        JSONObject root = new JSONObject();
                        try {
                            root.put("username", username);
                            root.put("activationKey", activationCode);
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityLaunch.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        String json = root.toString();
                        params.add(new PostParam("data", json));

                        String jString = api.POST(Strings.ApiUrl(), api.POST_DATA(params));
                        AuthStatus as = new JsonHandler().getHasAuth(jString);
                        if (as != null && as.authenticationExit == 0) {
                            SettingsHandler sh = new SettingsHandler();
                            sh.setBooleanSetting(ActivityLaunch.this, R.string.preference_validate, false);
                            sh.setBooleanSetting(ActivityLaunch.this, R.string.preference_setPassword, true);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.registerUserCode).setVisibility(View.GONE);
                                    HandlePasswordSetting();
                                }
                            });
                        }
                    }
                });


            }
        });

    }

    private void HandlePasswordSetting() {
        findViewById(R.id.registerUserLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.registerPassword).setVisibility(View.VISIBLE);

        final EditText et1 = (EditText) findViewById(R.id.registerPass1);
        final EditText et2 = (EditText) findViewById(R.id.registerPass2);

        findViewById(R.id.registerPassButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = et1.getText().toString();
                String password2 = et2.getText().toString();

                if (password1.equals(password2)) {
                    try {
                        JSONObject root = new JSONObject();
                        root.put("username", username);
                        root.put("password", getSHA256(password1));

                        String json = root.toString();
                        final ArrayList<PostParam> params = new ArrayList<>();
                        params.add(new PostParam("request", "initPass_user"));
                        params.add(new PostParam("data", json));

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Api api = new Api();
                                String result = api.POST(Strings.ApiUrl(), api.POST_DATA(params));
                                final AuthStatus as = new JsonHandler().getHasAuth(result);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (as != null && as.authenticationExit == 0) {
                                            new SettingsHandler().setBooleanSetting(ActivityLaunch.this, R.string.preference_setPassword, false);
                                            findViewById(R.id.registerUserLayout).setVisibility(View.GONE);
                                            findViewById(R.id.registerPassword).setVisibility(View.GONE);
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ValidateAuthentication();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ActivityLaunch.this, as.message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });


                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(ActivityLaunch.this, "Passwords did not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void Login() {
        final String username = ((EditText) findViewById(R.id.loginUser)).getText().toString();
        String password = null;
        try {
            password = getSHA256(((EditText) findViewById(R.id.loginPass)).getText().toString());
            JSONObject root = new JSONObject();
            try {
                root.put("username", username);
                root.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String json = root.toString();
            final ArrayList<PostParam> params = new ArrayList<>();
            params.add(new PostParam("request", "login_user"));
            params.add(new PostParam("data", json));

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Api api = new Api();
                    String result = api.POST(Strings.ApiUrl(), api.POST_DATA(params));
                    AuthStatus as = new JsonHandler().getHasAuth(result);
                    if (as != null && as.authenticationExit == 0 && as.authenticationToken.length() > 0) {
                        new SettingsHandler().setStringSetting(ActivityLaunch.this, R.string.preference_username, username);
                        new SettingsHandler().setStringSetting(ActivityLaunch.this, R.string.preference_AuthKey, as.authenticationToken);
                        Log.e("AuthKey", as.authenticationToken);
                        ValidateAuthentication();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Wrong student number or password, check again .", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }


    private String getSHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());
        byte[] passHash = md.digest();

        String hex = String.format("%064x", new BigInteger(1, passHash));
        System.out.println(hex);
        Log.e("SHA256-Hex", hex);
        return hex;
    }


    public class SplashAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mainLogo.clearAnimation();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mainLogo.getLayoutParams();
            lp.setMargins(0, 0, 0, lp.topMargin);
            mainLogo.setLayoutParams(lp);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}