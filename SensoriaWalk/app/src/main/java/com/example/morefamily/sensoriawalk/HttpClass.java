package com.example.morefamily.sensoriawalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by MoreFamily on 12/5/2015.
 */
public class HttpClass extends AsyncTask<String, String, String> {

    private HttpResponseCallBack mainClass;
    private String urlToUse;
    private String result;
    private JSONObject jObject;
    private StringBuilder sb;
    private boolean running;
    private ProgressDialog progressDialog;
    private Context mContext;

    public HttpClass()
    {
        urlToUse = null;
        result = null;
        jObject = null;
    }
    public HttpClass (HttpResponseCallBack mClass, String url, String urlParameter, Context activity) {
        mainClass = mClass;
        this.urlToUse = url;
        mContext = activity;
    }

    //Post Method
    public String POST(String inputUrl, String urlParameters) {
        {

            HttpURLConnection connection = null;
            String targetURL = inputUrl;
            URL url = null;
            try {
                url = new URL(targetURL);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }

            try {
                if (null != url) {
                    //App clientID
                    String credentials = "635434509433220695:1b53b8abd1bb472e97d2db299dbce79f";
                    String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    System.out.println(base64EncodedCredentials);
                    System.out.println("Content:" + urlParameters.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
                    connection.setRequestProperty("Content-Length", "" +
                            Integer.toString(urlParameters.getBytes().length));
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Language", "en-US");
                    connection.setRequestMethod("POST");
                    connection.setUseCaches(false);
                    //send the POST out
                    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                    out.write(urlParameters);
                    out.close();
                }

                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("Login Failed");
                }
                else
                {
                    System.out.println("Login Pass");
                    InputStream instream = connection.getInputStream();

                    int ch;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = instream.read()) != -1) {
                        sb.append((char) ch);
                    }
                    System.out.println(sb.toString());
                    result = sb.toString();
//                    mainClass.callBackMethod(result);
                    jObject = new JSONObject(sb.toString());
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
        @Override
        protected  void onPreExecute()
        {
            running = true;

            progressDialog = ProgressDialog.show(mContext,
                    "ProgressDialog",
                    "Wait!");

            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    running = false;
                }
            });
        }

        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], urls[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            mainClass.callBackMethod(result);
        }

    //Helper to Convert InputStream to JSON String
    private static String convertStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        try {

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (Exception e) {
                System.out.println("Stream Exception");
            }

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());

        }
        return total.toString();
    }
}
