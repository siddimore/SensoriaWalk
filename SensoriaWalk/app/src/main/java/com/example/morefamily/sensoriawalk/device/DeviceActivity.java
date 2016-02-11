package com.example.morefamily.sensoriawalk.device;

/**
 * Created by MoreFamily on 1/4/2016.
 */


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.bluetooth.BluetoothAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.morefamily.sensoriawalk.R;
import com.example.morefamily.sensoriawalk.SensoriaLib.SAAnklet;
import com.example.morefamily.sensoriawalk.SensoriaLib.SAAnkletInterface;
import com.example.morefamily.sensoriawalk.SensoriaLib.SAFoundAnklet;
import com.example.morefamily.sensoriawalk.UserLogin;


public class DeviceActivity extends Activity implements SAAnkletInterface {
    // Return Intent extra
    public static String EXTRA_DEVICE_MAC = "device_mac";
    public static String EXTRA_DEVICE_CODE = "device_code";
    public static String TAG = "DeviceActivity";
    public static final int DEVICE_SELECTION = 991;

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mDiscoveredDevicesArrayAdapter;

    SAAnklet anklet;
    private String selectedCode;
    private String selectedMac;

    SharedPreferences sharedpreferences;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            //anklet = new SAAnklet(this);
        anklet = SAAnklet.getInstance();
        anklet.init(this);
        anklet.startScan();
        setContentView(R.layout.activity_device);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Store the SelectrfCode and SelectedMac
            SAFoundAnklet deviceDiscovered = anklet.deviceDiscoveredList.get(arg2);
            selectedCode = deviceDiscovered.deviceCode;
            selectedMac = deviceDiscovered.deviceMac;
            Log.d(TAG, selectedCode + " " + selectedMac);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(EXTRA_DEVICE_MAC, selectedMac);
            editor.putString(EXTRA_DEVICE_CODE, selectedCode);
            editor.commit();

            //anklet.connect(selectedMac);
            finish();
        }
    };
    public void didDiscoverDevice(){
        // Set result CANCELED in case the user backs out
        Log.w("SensoriaLibrary", "Device Discovered!");

        Spinner s = (Spinner) findViewById(R.id.spinner);
        setResult(Activity.RESULT_CANCELED);

        Bundle extras = getIntent().getExtras();
        Boolean currentFound = false;
        String currentAddress = extras.getString(EXTRA_DEVICE_MAC);

        // Initialize array adapter
        mDiscoveredDevicesArrayAdapter = new ArrayAdapter(this, R.layout.item_template_device_name_item, anklet.deviceDiscoveredList);

        // Find and set up the ListView for paired devices
        ListView discoveredListView = (ListView) findViewById(R.id.paired_devices);
        discoveredListView.setAdapter(mDiscoveredDevicesArrayAdapter);
        discoveredListView.setOnItemClickListener(mDeviceClickListener);

        if(anklet.deviceDiscoveredList.size() > 0){
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            currentFound = true;
        }else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mDiscoveredDevicesArrayAdapter.add(noDevices);
        }

        // Find and set up the TextView for current device
        TextView textCurrent = (TextView) findViewById(R.id.text_current_device);
        if (currentFound) {
            textCurrent.setText(extras.getString(EXTRA_DEVICE_CODE));
        } else {
            // need to check
            textCurrent.setText(extras.getString(EXTRA_DEVICE_CODE));
            //textCurrent.setText(extras.getString(EXTRA_DEVICE_CODE) + "\nINVALID");
        }
    }
    public void didConnect(){
        // Shouldn't got here because we didn't connect in this activity
        Log.w("SensoriaLibrary", "Device Connected!");
    }
    public void didError(String message){
        Log.e(TAG, message);
    }
    public void didUpdateData(){
        System.out.println("Inside DeviceActivity didUpdateData Callback");
    }


}
