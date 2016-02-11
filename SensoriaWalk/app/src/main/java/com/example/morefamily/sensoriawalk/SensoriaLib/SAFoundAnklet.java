package com.example.morefamily.sensoriawalk.SensoriaLib;

/**
 * Created by MoreFamily on 1/4/2016.
 */
public class SAFoundAnklet {
    public String deviceCode;
    public String deviceMac;

    @Override
    public String toString() {
        return deviceCode + " (" + deviceMac + ")";
    }
}
