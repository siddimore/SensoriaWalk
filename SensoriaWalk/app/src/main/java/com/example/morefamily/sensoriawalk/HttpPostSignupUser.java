package com.example.morefamily.sensoriawalk;



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
 * Created by MoreFamily on 12/29/2015.
 */
public class HttpPostSignupUser extends HttpClass{

    private HttpResponseCallBack mainClass;
    private String mUrlToUse;
    private String mResult;
    private JSONObject mJObject;
    private StringBuilder mSb;
    private String mAccessToken;
    private SignUpModel mUserModel;
    private HashMap<String, String> mData;

    public HttpPostSignupUser()
    {
        mUrlToUse = null;
        mResult = null;
        mJObject = null;
        mAccessToken = null;
    }

public HttpPostSignupUser (HttpResponseCallBack mClass, String url, String token,JSONObject data) {
        mainClass = mClass;
        this.mUrlToUse = url;
        mAccessToken = token;
    mJObject = data;
    }


    @Override
    public String POST(String inputUrl, String accessToken) {
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
                connection = (HttpURLConnection) url.openConnection();
                String Bearer = "Bearer " + accessToken;
                connection.setRequestProperty("Authorization", Bearer);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                //send the POST out
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write((mJObject.toString()));
                writer.flush();
                writer.close();
                os.close();
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
                mainClass.callBackMethod(mResult);
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
