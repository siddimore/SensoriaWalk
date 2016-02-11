package com.example.morefamily.sensoriawalk;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.morefamily.sensoriawalk.R;
import com.example.morefamily.sensoriawalk.SensoriaLib.SAAnklet;
import com.example.morefamily.sensoriawalk.SensoriaLib.SAAnkletInterface;
import com.example.morefamily.sensoriawalk.device.DeviceActivity;
import com.example.morefamily.sensoriawalk.fragments.sessionplay;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;



/**
 * Created by MoreFamily on 1/13/2016.
 */
//public class Dashboard extends Fragment {

public class Dashboard extends Activity implements SAAnkletInterface {

    private SAAnklet anklet;
    private  LineGraphSeries<DataPoint> mHeelSeries;
    private  LineGraphSeries<DataPoint> mMtb1Series;
    private  LineGraphSeries<DataPoint> mMtb5Series;

    private static final int HEEL_INDEX = 2;
    private static final int MTB1_INDEX = 1;
    private static final int MTB5_INDEX = 0;


    private Double mCurrentX = 0d;
    private int mSampleCount = 0;
    private Boolean mPaused = false;

    private Boolean mPlotPressureNotAC = true;


    private double T = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        anklet = SAAnklet.getInstance();
        if(!anklet.connected) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String currentDeviceMAC =  prefs.getString(DeviceActivity.EXTRA_DEVICE_MAC, "NONE");
            anklet.changeDelegate(this);
            anklet.connect(currentDeviceMAC);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        GraphView graph = (GraphView)findViewById(R.id.graph);
        mHeelSeries = new LineGraphSeries<>();
        mHeelSeries.setColor(Color.BLUE);
        mHeelSeries.setTitle("Heel");

        mMtb1Series = new LineGraphSeries<>();
        mMtb1Series.setColor(Color.RED);
        mMtb1Series.setTitle("MTB 1");

        mMtb5Series =new LineGraphSeries<>();
        mMtb5Series.setColor(Color.GREEN);
        mMtb5Series.setTitle("MTB 5");
        //Sample Graph Test
        //Use Sessions API to get data from Cloud and chart it using GraphView
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
        //graph.addSeries(series);
        graph.addSeries(mHeelSeries);
        graph.addSeries(mMtb1Series);
        graph.addSeries(mMtb5Series);
        addFragment(R.id.lstFragment1, new sessionplay());
    }



    public void addFragment(int layoutId, Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(layoutId, fragment);
        ft.commit();}

    @Override
    protected void onStop() {
        super.onStop();
        anklet.disconnect();
    }

    @Override
    public void didDiscoverDevice() {

    }

    @Override
    public void didConnect() {
        Log.w("Dashboard  Activity", "Device Connected");
    }

    @Override
    public void didError(String message) {
        Log.e("Dashboard  Activity", message);
    }

    public void AnkletPause()
    {
        if(anklet!=null)
        {
            anklet.pause();
        }
    }

    public void AnkletStop()
    {
        if(anklet!=null)
        {
            anklet.disconnect();
            Intent workoutSummaryActivity = new Intent(Dashboard.this, workoutsummary.class);
            startActivity(workoutSummaryActivity);
        }
    }

    //Event Handler for incoming data
    @Override
    public void didUpdateData() {
        System.out.println("Inside DashBoardActivity didUpdateData Callback");
        T = T + 31.25;
        mSampleCount++;
        // Only plot 1 out of 5 samples
        if (mSampleCount % 5 == 0) {
            //Reset graph every minute
            if (mSampleCount % (1000 / 40 * 60) == 0) {
                mHeelSeries.resetData(new DataPoint[] {new DataPoint(0, anklet.heel)});
                mMtb1Series.resetData(new DataPoint[] {new DataPoint(0, anklet.mtb1)});
                mMtb5Series.resetData(new DataPoint[] {new DataPoint(0, anklet.mtb5)});
                mCurrentX = 0.0;
            } else if (mPlotPressureNotAC) {
                mHeelSeries.appendData(new DataPoint(mCurrentX, anklet.heel), true, 10);
                mMtb1Series.appendData(new DataPoint(mCurrentX, anklet.mtb1), true, 10);
                mMtb5Series.appendData(new DataPoint(mCurrentX, anklet.mtb5), true, 10);
            } else {
                mHeelSeries.appendData(new DataPoint(mCurrentX, anklet.accX), true, 10);
                mMtb1Series.appendData(new DataPoint(mCurrentX, anklet.accY), true, 10);
                mMtb5Series.appendData(new DataPoint(mCurrentX, anklet.accZ), true, 10);
            }

            Log.i("GoalsActivity", "S0:" + anklet.heel + ", S1:" + anklet.mtb1 + ", S2:" + anklet.mtb5 + ", x:" + mCurrentX);
        }
    }
}
