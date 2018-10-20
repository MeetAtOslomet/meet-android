package no.oslomet.meet.core;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import no.oslomet.meet.classes.PostParam;

public class Api
{
    public URL getUrl(String _url)
    {
        URL url = null;
        try { url = new URL(_url); }
        catch (MalformedURLException e) { e.printStackTrace(); }
        return url;
    }

    public String GET(String _url)
    {
        URL url = getUrl(_url);
        if (url != null)
        {
            try {
                HttpsURLConnection client = (HttpsURLConnection )url.openConnection();
                client.setSSLSocketFactory(Certificate.sslContext.getSocketFactory());
                client.setRequestMethod("GET");
                client.setReadTimeout(5000);
                client.setConnectTimeout(5000);
                client.setDoOutput(false);
                client.connect();
                return readStream(new InputStreamReader(url.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public String POST(String _url, String data)
    {
        String res = null;
        URL url = getUrl(_url);
        if (url != null)
        {
            try {
                HttpsURLConnection client = (HttpsURLConnection )url.openConnection();
                client.setSSLSocketFactory(Certificate.sslContext.getSocketFactory());
                client.setDoOutput(true);
                client.setRequestMethod("POST");

                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeBytes(data);
                dos.flush();
                //osw.close();

                InputStreamReader isr = new InputStreamReader(client.getInputStream());
                res = readStream(isr);
                isr.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public String POST_DATA(ArrayList<PostParam> param)
    {
        Uri.Builder builder = new Uri.Builder();
        for(PostParam pp : param)
        {
            builder.appendQueryParameter(pp.getKey(), pp.getData());
        }
        String query = builder.build().getEncodedQuery();
        return query;
    }


    private String readStream(InputStreamReader isr)
    {
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while(((line = br.readLine())) != null)
            {
                sb.append(line + "\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
