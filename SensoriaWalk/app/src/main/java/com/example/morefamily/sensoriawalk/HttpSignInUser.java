package com.example.morefamily.sensoriawalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by MoreFamily on 1/2/2016.
 */

public class HttpSignInUser extends HttpClass{

    private HttpResponseCallBack mainClass;
    private String mUrlToUse;
    private String mResult;
    private JSONObject mJObject;
    private StringBuilder mSb;
    private String mAccessToken;
    private Context mContext;
    private HashMap<String, String> mData;
    private String result = null;
    private boolean running;
    private ProgressDialog progressDialog;

    public HttpSignInUser()
    {
        mUrlToUse = null;
        mResult = null;
        mJObject = null;
        mAccessToken = null;
    }

    public HttpSignInUser (HttpResponseCallBack mClass, String url, String urlParameter, Context activity) {
        mainClass = mClass;
        this.mUrlToUse = url;
        mContext = activity;
    }


    @Override
    public String POST(String inputUrl, String urlParameters) {

        HttpURLConnection connection = null;
        String targetURL = inputUrl;
        targetURL = "https://test-auth.sensoriafitness.com/oauth20/token";
        URL url = null;
        try {
            url = new URL(targetURL);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        try {
            if (null != url) {
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
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        mainClass.callBackMethod(result);
    }

}
