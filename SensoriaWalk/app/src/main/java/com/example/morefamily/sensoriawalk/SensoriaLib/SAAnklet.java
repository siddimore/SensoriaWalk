package com.example.morefamily.sensoriawalk.SensoriaLib;

/**
 * Created by MoreFamily on 1/4/2016.
 */

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.sensoria.signal.COBSignalProcess;


public class SAAnklet extends SAFoundAnklet implements BluetoothAdapter.LeScanCallback {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private static final int length10 = 10;
    private static final int length13 = 13;
    private static final int length20 = 20;
    private static SAAnklet anklet = null;
    public boolean connected = false;

    public long mtb1 = 0;
    public long mtb5 = 0;
    public long heel = 0;
    public int tick = 0;
    public float accX = 0.0f;
    public float accY = 0.0f;
    public float accZ = 0.0f;
    private int length = 0;
    private byte[] revisionNumber;
    public ArrayList<SAFoundAnklet> deviceDiscoveredList = new ArrayList<SAFoundAnklet>();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mConnectedGatt; //mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_SCANNING = 3;
    private boolean isReading;

    public final static UUID UUID_SENSORIA_FAST_STREAMING_DATA = UUID.fromString("1cac2e60-0201-11e3-898d-0002a5d5c51b");

    private SAAnkletInterface iAnklet;
    private Context callerContext;


    Handler handler;

    private final static String TAG = SAAnklet.class.getSimpleName();

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private static final SAAnklet instance = new SAAnklet();
   // private static final SAAnklet instance = new SAAnklet();
    public SAAnklet() {
        //do nothing
    }

    public static SAAnklet getInstance()
    {
        return instance;

    }

    public void init(SAAnkletInterface delegate) {
        // Save the event object for later use.
        iAnklet = delegate;
        callerContext = (Context) delegate;

        handler = new Handler();

        //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        BluetoothManager manager = (BluetoothManager) callerContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            iAnklet.didError("No LE Support.");
            return;
        }
    }

    public void changeDelegate(SAAnkletInterface delegate)
    {
        iAnklet = delegate;
    }

    public void pause() {
        mBluetoothAdapter.stopLeScan(this);
    }



    public void connect(String selectedDeviceMac) {
        if (mConnectionState == STATE_SCANNING) {
            mBluetoothAdapter.stopLeScan(this);
        }
        mConnectionState = STATE_CONNECTING;
        deviceMac = selectedDeviceMac;
        if (deviceMac == null) {
            //Connect re-scanning devices
            deviceDiscoveredList.clear();
            mBluetoothAdapter.startLeScan(this);
        } else {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceMac);
            if (device == null) {
                iAnklet.didError("Device not found.  Unable to connect.");
                return;
            }

            connectOnTheMainThread(callerContext, device);
        }
    }

    public void disconnect() {
        mConnectionState = STATE_DISCONNECTED;

        //Disconnect from any active tag connection
        if (mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt = null;
        }
    }

    public void startScan() {
        mConnectionState = STATE_SCANNING;
        deviceDiscoveredList.clear();
        mBluetoothAdapter.startLeScan(this);
    }

    public void stopScan() {
        mConnectionState = STATE_DISCONNECTED;
        mBluetoothAdapter.stopLeScan(this);
    }

    /* BluetoothAdapter.LeScanCallback */

    private boolean foundDeviceCodeInArray(String foundDeviceCode) {

        for (SAFoundAnklet storedDeviceDiscovered : deviceDiscoveredList) {
            if (storedDeviceDiscovered.deviceCode.equals(foundDeviceCode)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        String deviceName = device.getName();
        String foundDeviceMac = device.getAddress();

        if (null != deviceName) {
            if (deviceName.startsWith("Sensoria-F1-")) {
                String foundDeviceCode = deviceName.substring(12);

                if (!foundDeviceCodeInArray(foundDeviceCode)) {
                    SAFoundAnklet deviceDiscovered = new SAFoundAnklet();

                    deviceDiscovered.deviceCode = foundDeviceCode;
                    deviceDiscovered.deviceMac = foundDeviceMac;

                    deviceDiscoveredList.add(deviceDiscovered);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iAnklet.didDiscoverDevice();
                        }
                    });

//                    if (mConnectionState == STATE_CONNECTING && deviceDiscovered.deviceMac == null && deviceDiscovered.deviceCode.equals(foundDeviceCode)) {
//                        stopScan();
//
//                        Log.i(TAG, "Connecting to " + foundDeviceCode + " after scan");
//                        connectOnTheMainThread(callerContext, device);
//                    }
                }
            }
        }
    }

    private void connectOnTheMainThread(final Context context, final BluetoothDevice device) {
        Log.d(TAG, "Inside Connect on Main Thread");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectedGatt = device.connectGatt(context, false, mGattCallback);
            }
        });
    }

    private int getLength(final BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        int length = data.length;
        return length;
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */
                gatt.discoverServices();
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                /*
                 * If at any point we disconnect, send a message to clear the weather values
                 * out of the UI
                 */
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                /*
                 * If there is a failure at any stage, simply disconnect
                 */
                gatt.disconnect();
            }
        }


        private void doRead(BluetoothGattCharacteristic gattC) {
            //....internal accounting stuff up here....
            // characteristic = mConnectedGatt.getService(mCurrServiceUUID).getCharacteristic(mCurrCharacteristicUUID);
            if (mConnectedGatt != null) {
                do {
                    isReading = mConnectedGatt.readCharacteristic(gattC);
                } while (!isReading);

                System.out.println("Is reading in progress? " + Boolean.toString(isReading));
                System.out.println("Printing elements by thread" + Thread.currentThread().getName());
                // Wait for read to complete before continuing.
                {
                    synchronized (readLock) {
                        try {
                            readLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "Services Discovered: " + status);
            boolean flag = false;
            boolean cflag = false;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Loops through available GATT Services to look for Sensoria Fitness Streaming Service
                //UUID.fromString("1cac2e60-0200-11e3-898d-0002a5d5c51b")
                for (BluetoothGattService gattService : gatt.getServices())
                {
                    if (gattService.getUuid().toString().compareToIgnoreCase("1cac2e60-0100-11e3-898d-0002a5d5c51b") == 0) {
                        //readBTLECharacteristic(gattService);
                        List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                            if (gattCharacteristic.getUuid().toString().compareToIgnoreCase("1cac2e60-0101-11e3-898d-0002a5d5c51b") == 0) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        doRead(gattCharacteristic);
                                    }
                                }).start();
                            }
                        }
                    }
                    if (gattService.getUuid().toString().compareToIgnoreCase("1cac2e60-0200-11e3-898d-0002a5d5c51b") == 0) {
                        List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                        // Loops through available Characteristics to find streaming service
                        //UUID.fromString("1cac2e60-0201-11e3-898d-0002a5d5c51b")
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                            if (gattCharacteristic.getUuid().toString().compareToIgnoreCase("1cac2e60-0201-11e3-898d-0002a5d5c51b") == 0) {
                                final int charaProp = gattCharacteristic.getProperties();

                                // Confirm that this supports notify
                                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                    setCharacteristicNotification(gattCharacteristic, true);

                                    mConnectionState = STATE_CONNECTED;  //TODO: JACOPO: Check if not just set in onConnectionStateChange

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            iAnklet.didConnect();
                                        }
                                    });

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }


        /**
         * Enables or disables notification on a give characteristic.
         *
         * @param characteristic Characteristic to act on.
         * @param enabled If true, enable notification.  False otherwise.
         */
        private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                   boolean enabled) {
            if (mBluetoothAdapter == null || mConnectedGatt == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iAnklet.didError("BluetoothAdapter not initialized");
                    }
                });

                return;
            }

            mConnectedGatt.setCharacteristicNotification(characteristic, enabled);

            byte[] enableNotification = (enabled) ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            UUID uuidCharacteristic = characteristic.getUuid();

            Log.d(TAG, "setCharacteristicNotification: UUID: " + uuidCharacteristic.toString());

            List<BluetoothGattDescriptor> bluetoothGattDescriptors = characteristic.getDescriptors();

            BluetoothGattDescriptor descriptor = bluetoothGattDescriptors.get(1);
            descriptor.setValue(enableNotification);
            mConnectedGatt.writeDescriptor(descriptor);
        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(TAG, "Remote RSSI: " + rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS && (characteristic.getUuid().toString().equals("1cac2e60-0101-11e3-898d-0002a5d5c51b"))) {
                System.out.println("Revision Read Called Successfully");
                System.out.println("On thread: " + Thread.currentThread().getName());

                byte[] value = revisionNumber = characteristic.getValue();
                StringBuilder sb = new StringBuilder();
                for (byte b : value) {
                    sb.append(String.format("%02X", b));
                }

                System.out.println("Read characteristic value: " + sb.toString());

                synchronized (readLock) {
                    readLock.notifyAll();
                }
            }
        }


        private float convertToGs(long b) {
            float val;

            if (b < 512) {
                val = 0.03125f * (float) b;
            } else {
                val = 0.03125f * -(1024 - (float) b);
            }

            return val;
        }

        private float byteToGs(int b) {
            float val;

            if (b < 128) {
                val = 0.03125f * (float) b;
            } else {
                val = 0.03125f * -(256 - (float) b);
            }

            return val;
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            final BluetoothGattCharacteristic characteristic) {
            if (UUID_SENSORIA_FAST_STREAMING_DATA.equals(characteristic.getUuid())) {

                if (0 == length) {
                    length = getLength(characteristic);
                } else {
                    BluetoothFilter(characteristic);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iAnklet.didUpdateData();
                    }
                });
            } else {
                // For all other profiles, writes the data formatted in HEX.
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));

                    Log.d(TAG, new String(data) + "\n" + stringBuilder.toString());
                }
            }
        }

        private void BluetoothFilter(BluetoothGattCharacteristic characteristic) {
            if (0 != length) {
                if (length10 == length) {
                    mtb1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 3);
                    mtb5 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5);
                    heel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7);
                    tick = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 9);
                    accX = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
                    accY = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
                    accZ = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2));
                } else if (length13 == length) {
                    if (revisionNumber[0] <= 12) {    // A13
                        mtb1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 3);
                        mtb5 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5);
                        heel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7);
                        tick = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 9);
                        accX = byteToGs(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
                        accY = byteToGs(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
                        accZ = byteToGs(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2));

                    } else { // B13
                        BluetoothGattCharacteristic c = characteristic;
                        int msgType = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                        msgType = (msgType >> 4);
                        System.out.println(Integer.toBinaryString(msgType));
//                    // msgType should be 0xA. NOTE: need to check G & Hz ??
                        if (msgType == 0xA) {
                            System.out.println("Msg Type 0xA!!!!!");
                            int tick1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
                            int tick2 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
                            int tick3 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4);
                            int tick = ((tick1 << 16) | (tick2 << 8)) | (tick3);
                            long ttag = ((tick & 0x00FF0000) >> 16) | (tick & 0x0000FF00) | ((tick & 0x000000FF) << 16);
                            System.out.println(Long.toString(ttag));
                            long s2 = mtb1 = (0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5)) << 2) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6) >> 6)));
                            System.out.println(Long.toString(s2));
                            long s1 = mtb5 = (0x3FF & (((characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6)) << 4) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7) >> 4)));
                            System.out.println(Long.toString(s1));
                            long s0 = heel = (0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7)) << 6) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 8) >> 2)));
                            System.out.println(Long.toString(s0));
                            accX = convertToGs(0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 8)) << 8) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 9))));
                            System.out.println(Float.toString(accX));
                            accY = convertToGs(0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 10)) << 2) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 11) >> 6)));
                            System.out.println(Float.toString(accY));
                            accZ = convertToGs(0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 11)) << 4) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 12) >> 4)));
                            System.out.println(Float.toString(accZ));
                        }
                    }
                } else if (length20 == length) {
                    if (revisionNumber[0] <= 12) {    // A13
                        mtb1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 3);
                        mtb5 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5);
                        heel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7);
                        tick = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 9);
                        accX = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
                        accY = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
                        accZ = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2));

                    } else { // B20
                        int msgType = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                        msgType = (msgType >> 4);
                        System.out.println(Integer.toBinaryString(msgType));
                        System.out.println("Msg Type B20!!!!!");
                        long s2 = mtb1 = (0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5)) << 2) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6) >> 6)));
                        System.out.println(Long.toString(s2));
                        long s1 = mtb5 = (0x3FF & (((characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6)) << 4) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7) >> 4)));
                        System.out.println(Long.toString(s1));
                        long s0 = heel = (0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7)) << 6) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 8) >> 2)));
                        System.out.println(Long.toString(s0));
                        tick = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 9);
                        accX = convertToGs(0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 8)) << 8) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 9))));
                        System.out.println(Float.toString(accX));
                        accY = convertToGs(0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 10)) << 2) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 11) >> 6)));
                        System.out.println(Float.toString(accY));
                        accZ = convertToGs(0x3FF & (((long) (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 11)) << 4) | (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 12) >> 4)));
                        System.out.println(Float.toString(accZ));
                    }
                }
            }
        }
    };
}
