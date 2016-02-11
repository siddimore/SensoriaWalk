package com.example.morefamily.sensoriawalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.morefamily.sensoriawalk.SensoriaLib.SAAnklet;
import com.example.morefamily.sensoriawalk.device.DeviceActivity;
import com.example.morefamily.sensoriawalk.fragments.sessionplay;
import com.example.morefamily.sensoriawalk.fragments.setupData;


/**
 * Created by MoreFamily on 12/24/2015.
 */
public class Goals extends FragmentActivity{

//    ArrayList<HashMap<String, String>> stepsArray;
//    ArrayList<HashMap<String, String>> distanceArray;
//    ArrayList<HashMap<String, String>> timeArray;
    ArrayList<String> stepsArray;
    ArrayList<String> distanceArray;
    ArrayList<String> timeArray;
    private boolean signOut = false;
    private Spinner stepSP;
    private Spinner distanceSP;
    private Spinner timeSP;
    private Button startButton;
    public static String currentDeviceMAC = null;
    public static String currentDeviceCODE = null;
    SharedPreferences sharedpreferences;
    public static final String DeviceMac = "MacAddress";
    public static final String DeviceCode = "Code";
    private SAAnklet anklet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.goals);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentChildViewGroup, new setupData())
                    .commit();
        }

        setContentView(R.layout.goals);
//        getDataArray();
//
//        stepSP=(Spinner)findViewById(R.id.Stepsspinner);
//        stepSP.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stepsArray));
//
//        distanceSP=(Spinner)findViewById(R.id.DistanceSpinner);
//        distanceSP.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, distanceArray));
//
//        timeSP=(Spinner)findViewById(R.id.Timespinner);
//        timeSP.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, timeArray));
//
//        startButton = (Button)findViewById(R.id.startButton);

    }


//    public void getDataArray()
//    {
//        DataLookUp sdO = new DataLookUp("StepsGoal.json", "Steps", this);
//        stepsArray = sdO.convertJSONArrayToList();
//
//        DataLookUp ddO = new DataLookUp("DistanceGoal.json", "Distance", this);
//        distanceArray = ddO.convertJSONArrayToList();
//        distanceArray = ConvertDistance.ConvertDistanceArray(distanceArray);
//
//        DataLookUp tdO = new DataLookUp("TimeGoal.json", "Time", this);
//        timeArray = tdO.convertJSONArrayToList();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DeviceActivity.DEVICE_SELECTION:
                if (resultCode == Activity.RESULT_OK) {
                    currentDeviceMAC = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_MAC);
                    currentDeviceCODE = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_CODE);

                    //Save Token to preference
                    sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor deviceDetail = sharedpreferences.edit();
                    deviceDetail.putString(DeviceMac, currentDeviceMAC);
                    deviceDetail.putString(DeviceCode,  currentDeviceCODE);
                    deviceDetail.commit();

                    if (currentDeviceMAC.isEmpty()) {
                        Toast.makeText(Goals.this, "Please Select a Device", Toast.LENGTH_SHORT).show();
                    }
                } else {
                }
                break;
        }
    }

    public void SelectDevice(View v)
    {
        currentDeviceMAC = DeviceActivity.EXTRA_DEVICE_MAC;
        currentDeviceCODE = DeviceActivity.EXTRA_DEVICE_CODE;
        Intent searchDeviceIntent = new Intent(Goals.this, DeviceActivity.class);
        searchDeviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE_CODE, (currentDeviceCODE.isEmpty() ? "<NONE>" : currentDeviceCODE));
        searchDeviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE_MAC, currentDeviceMAC);
        startActivityForResult(searchDeviceIntent, DeviceActivity.DEVICE_SELECTION);
    }

    public void StartSession(View v)
    {
        if(currentDeviceCODE == null || currentDeviceMAC == null)
        {
            Toast.makeText(Goals.this, "Please Connect Anklet Device", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //TestCode change this to connect
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            currentDeviceMAC =  prefs.getString(DeviceActivity.EXTRA_DEVICE_MAC, "NONE");
            currentDeviceCODE = prefs.getString(DeviceActivity.EXTRA_DEVICE_CODE, "NONE");
//            anklet = SAAnklet.getInstance();
//            anklet.disconnect();
            LaunchDashboard(v);
        }
//        currentDeviceMAC = DeviceActivity.EXTRA_DEVICE_MAC;
//        currentDeviceCODE = DeviceActivity.EXTRA_DEVICE_CODE;
//        Intent searchDeviceIntent = new Intent(Goals.this, DeviceActivity.class);
//        searchDeviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE_CODE, (currentDeviceCODE.isEmpty() ? "<NONE>" : currentDeviceCODE));
//        searchDeviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE_MAC, currentDeviceMAC);
//        startActivityForResult(searchDeviceIntent, DeviceActivity.DEVICE_SELECTION);

        //Begin the transaction
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.fragmentChildViewGroup, new sessionplay());
//        ft.commit();
    }

    public void LaunchDashboard(View c)
    {
        Intent dashBoard = new Intent(Goals.this, Dashboard.class);
        startActivity(dashBoard);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.fragmentChildViewGroup, new Dashboard());
//        ft.commit();
    }


    @Override
    public void onBackPressed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        signOut = prefs.getBoolean("loginKey", false);
        if(signOut) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    }).create().show();
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Not Logged in")
                    .setMessage("Please Login").create().show();;
        }
    }
}
