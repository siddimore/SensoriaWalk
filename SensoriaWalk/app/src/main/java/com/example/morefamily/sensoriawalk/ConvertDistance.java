package com.example.morefamily.sensoriawalk;

import java.util.ArrayList;

/**
 * Created by MoreFamily on 12/25/2015.
 */
public class ConvertDistance{

    public static ArrayList<String>  ConvertDistanceArray(ArrayList<String> distanceArray)
    {
        int i =0;
        for (String d:distanceArray
             ) {
            double dinMi = Double.parseDouble(d.toString()) / 1.60934;
            dinMi = (double) Math.round(dinMi * 100) / 100;
            distanceArray.set(i, Double.toString(dinMi));
            ++i;
        }
        return distanceArray;
    }

}
