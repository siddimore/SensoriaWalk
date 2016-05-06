package com.example.morefamily.sensoriawalk;

import android.content.Context;
import android.net.Uri;
import android.util.Xml;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by MoreFamily on 12/1/2015.
 */

public class OauthClient {
   //region Vars
    private String signupToken = null;
    private String loginToken = null;
    private String authSrvRootUrl = "https://test-auth.sensoriafitness.com/";
    String clientId = "635434509433220695";
    String secretKey = "1b53b8abd1bb472e97d2db299dbce79f";
    private String returnResult;
    private  JSONObject jsonObject;
    private Context mContext;
    private HttpResponseCallBack mCallback;

    public OauthClient(Context activity, HttpResponseCallBack callBack) {
        mContext = activity;
        mCallback = callBack;
        if (BuildConfig.DEBUG) {
            authSrvRootUrl = "https://test-auth.sensoriafitness.com/";
        } else
            authSrvRootUrl = "https://auth.sensoriafitness.com/";
    }

    public void GetTokenAsync(String Scopes)
    {
        AuthToken token = null;
        Scopes = Scopes + "&grant_type=client_credentials";
        HttpClass client = new HttpClass(mCallback, this.authSrvRootUrl, Scopes, mContext );
        client.execute(authSrvRootUrl + "/oauth20/token", Scopes);
    }

    public void GetCredentialsTokenAsync(String Scopes)
    {
        AuthToken token = null;
        Scopes = Scopes + "&grant_type=password";
        HttpSignInUser client = new HttpSignInUser(mCallback, this.authSrvRootUrl, Scopes, mContext);
        client.execute(authSrvRootUrl + "/oauth20/token", Scopes);
    }

    public void PostSignupUser(SignUpModel model)
    {
        JSONObject login = null;
            try {
                login = new JSONObject();
                login.put("Email", "cyanswimming@gmail.com");
                login.put("FirstName", "Test");
                login.put("LastName", "Sensoria");
                login.put("Password", "TestPassword");
                login.put("SkipActivation", Boolean.toString(true));
                login.put("Status", Integer.toString(0));
                login.put("ReturnUrl", null);

            }
            catch (JSONException ex) {
            System.out.println("JsonException" + ex.toString());
        }
            HttpPostSignupUser client = new HttpPostSignupUser(mCallback, this.authSrvRootUrl, returnResult,login);
            client.execute(authSrvRootUrl+"/api/1.0/SignUp/SignUpUser", signupToken);
    }
}
