package com.example.morefamily.sensoriawalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by MoreFamily on 11/25/2015.
 */
public class UserLogin extends Activity implements HttpResponseCallBack{

    private String userName;
    private String passWord;
    private AuthToken token;
    private static String OAUTH_SCOPE = "scope=users.read users.write shoes.read shoes.write sessions.read sessions.write firmware.read settings.read feedback.write";
    private OauthClient authClient;
    private static String loginString;
    private EditText mName;
    private EditText mPassword;
    SharedPreferences sharedpreferences;
    public static final String accessToken = "tokenKey";
    public static final String loggedin = "loginKey";
    public static final String loggedout = "logoutKey";
    public static final String loginUsername = "usernameKey";
    public static final String loginPassword = "passwordKey";
    private static Context mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        mName = (EditText)findViewById(R.id.signinEmail);
        mPassword = (EditText)findViewById(R.id.signinPassword);
        mcontext = getApplicationContext();
    }

    public void CheckSignIn(View view) {
        userName = mName.getText().toString();
        System.out.println("Username:"+userName);
        passWord = mPassword.getText().toString();
        System.out.println("Password:" + passWord);
        loginString = "username="+userName+"&password="+passWord+"&"+OAUTH_SCOPE;
        authClient = new OauthClient(UserLogin.this, this);
        authClient.GetCredentialsTokenAsync(loginString);
    }

    @Override
    public void callBackMethod(String resultString) {
        try {
            System.out.println("userprofiledata" + resultString);
            JSONObject jsonObject = new JSONObject(resultString);
            String token = jsonObject.getString("access_token");
            //To-Do
            //Save Token to preference
            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(accessToken, token);
            editor.putBoolean(loggedin, true);
            editor.putString(loginUsername, userName);
            editor.putString(loginPassword, passWord);
            editor.commit();
            Intent goalActivity = new Intent(UserLogin.this, Goals.class);
            startActivity(goalActivity);

        } catch (JSONException ex) {
            System.out.println("JsonException" + ex.toString());
        }
    }
}
