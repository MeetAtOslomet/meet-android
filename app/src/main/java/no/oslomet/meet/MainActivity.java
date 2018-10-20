package no.oslomet.meet;

import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import no.oslomet.meet.classes.PostParam;
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



    protected void onResume()
    {
        super.onResume();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run()
            {
                Api api = new Api();
                final String response = api.GET(Strings.Heartbeat());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = MainActivity.this.findViewById(R.id.HeartBeat);
                        tv.append(response);
                    }
                });

                ArrayList<PostParam> pptest = new ArrayList<>();
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
                });
            }
        });
    }
}
