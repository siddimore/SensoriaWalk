package com.example.morefamily.sensoriawalk.SensoriaLib;

/**
 * Created by MoreFamily on 1/4/2016.
 */
public interface SAAnkletInterface {
    public void didDiscoverDevice();
    public void didConnect();
    public void didError(String message);
    public void didUpdateData();
}
