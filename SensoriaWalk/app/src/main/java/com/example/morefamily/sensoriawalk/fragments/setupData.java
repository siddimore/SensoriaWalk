package com.example.morefamily.sensoriawalk.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.morefamily.sensoriawalk.ConvertDistance;
import com.example.morefamily.sensoriawalk.DataLookUp;
import com.example.morefamily.sensoriawalk.R;

import java.util.ArrayList;

/**
 * Created by MoreFamily on 12/26/2015.
 */
public class setupData extends Fragment{

    ArrayList<String> stepsArray;
    ArrayList<String> distanceArray;
    ArrayList<String> timeArray;

    private Spinner stepSP;
    private Spinner distanceSP;
    private Spinner timeSP;
    private Button startButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.setupfragment, parentViewGroup, false);

        getDataArray();

        stepSP=(Spinner)rootView.findViewById(R.id.Stepsspinner);
        stepSP.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, stepsArray));

        distanceSP=(Spinner)rootView.findViewById(R.id.DistanceSpinner);
        distanceSP.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, distanceArray));

        timeSP=(Spinner)rootView.findViewById(R.id.Timespinner);
        timeSP.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, timeArray));
        return rootView;
       //startButton = (Button)rootView.findViewById(R.id.startButton);
    }


    public void getDataArray()
    {
        DataLookUp sdO = new DataLookUp("StepsGoal.json", "Steps", getActivity().getApplicationContext());
        stepsArray = sdO.convertJSONArrayToList();

        DataLookUp ddO = new DataLookUp("DistanceGoal.json", "Distance", getActivity().getApplicationContext());
        distanceArray = ddO.convertJSONArrayToList();
        distanceArray = ConvertDistance.ConvertDistanceArray(distanceArray);

        DataLookUp tdO = new DataLookUp("TimeGoal.json", "Time", getActivity().getApplicationContext());
        timeArray = tdO.convertJSONArrayToList();
    }
}
