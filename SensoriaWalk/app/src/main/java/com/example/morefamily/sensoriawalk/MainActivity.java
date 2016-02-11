package com.example.morefamily.sensoriawalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.morefamily.sensoriawalk.Stat.DBStatistics;
import com.example.morefamily.sensoriawalk.dataBase.MySqliteHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends Activity implements  HttpResponseCallBack{

    String mUserName;
    String mPassword;
    boolean mLoggedIn;
    String loginString;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Button signUpButton = (Button)(findViewById(R.id.mainPageSignUp));
        Button loginButton = (Button)(findViewById(R.id.mainPageLogin));

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);

                // PERMISSION_REQUEST_COARSE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* (non-Javadoc)
   * @see android.app.Activity#onRestart()
   */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MainActivity", "On Restart .....");
        loadFromSharedPreferences();
    }

    /* (non-Javadoc)
    * @see android.app.Activity#onResume()
    */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "On Resume .....");
        loadFromSharedPreferences();
    }

    /* (non-Javadoc)
    * @see android.app.Activity#onStart()
    */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainActivity", "On Start .....");

    }

    public void Login(View view) {

        Intent i = new Intent(MainActivity.this, UserLogin.class);
        startActivity(i);

    }

    public void SignUp(View view) {

        Intent i = new Intent(MainActivity.this, UserSignUp.class);
        startActivity(i);
    }


    private void loadFromSharedPreferences()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mLoggedIn = prefs.getBoolean(UserLogin.loggedin, false);
        mUserName =  prefs.getString(UserLogin.loginUsername, "NONE");
        mPassword = prefs.getString(UserLogin.loginPassword, "NONE");

        if (mLoggedIn)
        {
            DBTest();
            String OAUTH_SCOPE = "scope=users.read users.write shoes.read shoes.write sessions.read sessions.write firmware.read settings.read feedback.write";
            loginString = "username="+mUserName+"&password="+mPassword+"&"+OAUTH_SCOPE;
            OauthClient authClient = new OauthClient(MainActivity.this, this);
            authClient.GetCredentialsTokenAsync(loginString);
            Intent loginActivity = new Intent(MainActivity.this, UserLogin.class);
            startActivity(loginActivity);
        }
    }



    @Override
    public void callBackMethod(String resultString) {
        try {
            System.out.println("userprofiledata" + resultString);
            JSONObject jsonObject = new JSONObject(resultString);
            String token = jsonObject.getString("access_token");
            Intent goalActivity = new Intent(MainActivity.this, Goals.class);
            startActivity(goalActivity);

        } catch (JSONException ex) {
            System.out.println("JsonException" + ex.toString());
        }
    }

    private void DBTest()
    {
        MySqliteHelper db = new MySqliteHelper(this);

        /**
         * CRUD Operations
         * */
        // Test add Stat
        db.addStat(new DBStatistics("10:30AM", "11:00AM","2mPH", "1mile", "4000"));
        db.addStat(new DBStatistics("10:30AM", "11:00AM","3mPH", "1mile", "5000"));


        // get all Stats
        List<DBStatistics> list = db.getAllStats();

        // delete one book
        //db.deleteStat(list.get(0));

        // get all books
        //db.getAllStats();
    }
}
