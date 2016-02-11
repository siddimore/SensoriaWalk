package com.example.morefamily.sensoriawalk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by MoreFamily on 11/25/2015.
 */
public class UserSignUp extends Activity implements HttpResponseCallBack{

    private UserProfile profileInfo;
    private EditText Name;
    private EditText Weight;
    private EditText Height;
    private EditText Gender;
    private EditText Password;
    private EditText Email;
    private EditText DateOfBirth;
    private static String OAUTH_SCOPE = "scope=users.signup users.skipactivation";
    private OauthClient authClient;
//    private static String CLIENT_ID = "635217432234679916";
//    private static String CLIENT_SECRET ="551925930bad4856b2be13865c621342";
//    private static String REDIRECT_URI="http://www.sensoriafitness.com";
//    private static String GRANT_TYPE="authorization_code";
//    private static String TOKEN_URL ="https://auth.sensoriafitness.com/oauth20/token";
//    private static String OAUTH_URL ="https://auth.sensoriafitness.com/authentication/login";
//private List<String> scopes = Arrays.asList("users.read", "users.write", "shoes.read", "shoes.write", "sessions.read", "sessions.write", "firmware.read", "settings.read", "feedback.write");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupuser);

        Button signUpButton = (Button)(findViewById(R.id.signUp));

    }

    public void SignUserUp(View view)
    {
        Name = (EditText)findViewById(R.id.Name);
        DateOfBirth = (EditText)findViewById(R.id.DOB);
        Height = (EditText)findViewById(R.id.height);
        Weight = (EditText)findViewById(R.id.weight);
        Password = (EditText)findViewById(R.id.password);
        Gender = (EditText)findViewById(R.id.GenderInput);
        profileInfo = new UserProfile(Name.getText().toString(),
                DateOfBirth.getText().toString(),
                Gender.getText().toString(),
                Weight.getText().toString(),
                Height.getText().toString());

        authClient = new OauthClient(UserSignUp.this, this);
        authClient.GetTokenAsync(OAUTH_SCOPE);
        authClient.PostSignupUser(null);
    }

    @Override
    public void callBackMethod(String resultString) {
        try {
            System.out.println("userprofiledata" + resultString);
            JSONObject jsonObject = new JSONObject(resultString);
            String token = jsonObject.getString("access_token");
            //PostSignupUser(null);
        } catch (JSONException ex) {
            System.out.println("JsonException" + ex.toString());
        }
    }

}
