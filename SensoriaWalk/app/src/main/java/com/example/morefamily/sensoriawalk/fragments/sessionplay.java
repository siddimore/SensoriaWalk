package com.example.morefamily.sensoriawalk.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.morefamily.sensoriawalk.R;

/**
 * Created by MoreFamily on 12/26/2015.
 */
public class sessionplay extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.session, parentViewGroup, false);
        return rootView;
    }

}
