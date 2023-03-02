package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.example.myapplication.BleUtils;
import static java.util.UUID.fromString;

import static android.bluetooth.BluetoothProfile.GATT;

public class BLEController{

    private static BLEController instance;

    private BluetoothLeScanner scanner;
    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager bluetoothManager;
    private ThunderBoardSensorMotion sensorMotion;

    BluetoothGattCharacteristic mAcceleration;

    private BluetoothGattCharacteristic btGattChar = null;
    private BluetoothGattDescriptor btGattDesc = null;

    private ArrayList<BLEControllerListener> listeners = new ArrayList<>();
    private HashMap<String, BluetoothDevice> devices = new HashMap<>();
    public Boolean isAccelerationNotificationEnabled;
    public static int HalfStepCounter = 0;
    List<BluetoothGattCharacteristic> chars = new ArrayList<>();

    // Acceleration
    float magnitude = 0;
    float prev_magnitude = 0;
    int action_status = 0;  // 0: no data, 1: resting, 2: walking

    private BLEController(Context ctx) {
        this.bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public static BLEController getInstance(Context ctx) {
        if (null == instance)
            instance = new BLEController((ctx));

        return instance;
    }

    public void addBLEControllerListener(BLEControllerListener l) {
        if (!this.listeners.contains(l))
            this.listeners.add(l);
    }

    public void removeBLEControllerListener(BLEControllerListener l) {
        this.listeners.remove(l);
    }

    public void init() {
        this.devices.clear();
        this.scanner = this.bluetoothManager.getAdapter().getBluetoothLeScanner();
        scanner.startScan(bleCallback);
    }

    private ScanCallback bleCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!devices.containsKey(device.getAddress()) && isThisTheDevice(device)) {
                deviceFound(device);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                BluetoothDevice device = sr.getDevice();
                if (!devices.containsKey(device.getAddress()) && isThisTheDevice(device)) {
                    deviceFound(device);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i("[BLE]", "scan failed with errorcode: " + errorCode);
        }
    };

    private boolean isThisTheDevice(BluetoothDevice device) {
        return null != device.getName() && device.getName().startsWith("Thunderboard #61545");
    }

    private void deviceFound(BluetoothDevice device) {
        this.devices.put(device.getAddress(), device);
        fireDeviceFound(device);
    }

    public void connectToDevice(String address) {
        Log.i("[BLE]","inside connect to device method");
        this.device = this.devices.get(address);
        this.scanner.stopScan(this.bleCallback);
        Log.i("[BLE]", "connect to device " + device.getAddress());
        this.bluetoothGatt = device.connectGatt(null, false, this.bleConnectCallback);
    }

    private BluetoothGattCallback bleConnectCallback = new BluetoothGattCallback() {
        String str;
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("[BLE]", "start service discovery " + bluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                btGattChar = null;
                Log.w("[BLE]", "DISCONNECTED with status " + status);
                fireDisconnected();
            } else {
                Log.i("[BLE]", "unknown state " + newState + " and status " + status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BluetoothGattService mservice = bluetoothGatt.getService(ThunderBoardUuids.UUID_SERVICE_ACCELERATION_ORIENTATION);
            mAcceleration = mservice.getCharacteristic(ThunderBoardUuids.UUID_CHARACTERISTIC_ACCELERATION);

            for (BluetoothGattService service : gatt.getServices()) {
                Log.i("BLE", String.valueOf(service));

                if (service.getUuid().equals(ThunderBoardUuids.UUID_SERVICE_ACCELERATION_ORIENTATION)) {
                    Log.i("BLE", "Inside Acceleration Service");
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        Log.i("BLE", "Inside Acceleration Characteristic");
                        if (characteristic.getUuid().equals(ThunderBoardUuids.UUID_CHARACTERISTIC_ACCELERATION)) {
                            Log.i("BLE", "Char = " + ThunderBoardUuids.UUID_CHARACTERISTIC_ACCELERATION);
                            Log.i("GATT", "gatt = " +gatt);
                            Log.i("CHAR","characteristic= "+characteristic);
                            btGattChar = characteristic;
                            fireConnected();
                            Log.i("BLE", "Acceleration Tracking Added");
                        }
                    }
                }
            }
        }

        // Acceleration only
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            UUID uuid = characteristic.getUuid();
            byte[] ba = characteristic.getValue();
            Log.i("BLE","Inside onCharacteristicChanged");
            Log.i("BLE","Value of ba= "+ba);

            if (ba == null || ba.length == 0) {
                Log.i("BLE", "characteristic is not initialized");
            } else {
                if (ThunderBoardUuids.UUID_CHARACTERISTIC_ACCELERATION.equals(uuid)) {
                    int accelerationX = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
                    int accelerationY = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 2);
                    int accelerationZ = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 4);
                    Log.i("BLE", "Acceleration Test : " + String.valueOf(accelerationX) + " " + String.valueOf(accelerationY) + " " + String.valueOf(accelerationZ));
                    magnitude = (float) Math.sqrt((accelerationX * accelerationX) + (accelerationY * accelerationY) + (accelerationZ * accelerationZ));

                    if (magnitude > 1030) {
                        if (magnitude < prev_magnitude) {
                            action_status = 2;  // walking
                            Log.d("BLE","WALKING");
                            HalfStepCounter = HalfStepCounter + 1;
//                            //We update the surrounding info to display it and send it to Cloud *Diksha use this, from Isra
//                            surrounding[4] = "WALKING";
                            MainActivity.progressBar.setProgress(HalfStepCounter/2);
                            MainActivity.progressText.setText(HalfStepCounter/2 + " steps completed");
                            Log.d("PEDOMETER","-----------------------"+HalfStepCounter/2);

                        }
                        prev_magnitude = magnitude;
                    } else {
                        action_status = 1;  // resting
                        Log.d("BLE","RESTING");
//                        //We update the surrounding info to display it and send it to Cloud *Diksha use this, from Isra
//                        surrounding[4] = "RESTING";
                    }
                    Log.d("BLE", "Acceleration Action Code (1: resting, 2: walking) : " + String.valueOf(action_status));
                    chars.add(characteristic);

//                    //WE UPDATE FIREBASE SURROUNDING LOG
//                    createEnvDoc(surrounding); //*IHQ
                }
            }
        }
    };

    private void setNotifySensor(BluetoothGatt gatt) {
        BluetoothGattCharacteristic characteristic = gatt.getService(ThunderBoardUuids.UUID_SERVICE_ACCELERATION_ORIENTATION).getCharacteristic(ThunderBoardUuids.UUID_CHARACTERISTIC_ACCELERATION);
        gatt.setCharacteristicNotification(characteristic, true);

        BluetoothGattDescriptor desc = characteristic.getDescriptor(ThunderBoardUuids.UUID_DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION);
        Log.d("BLE", "Descriptor is " + desc); // this is not null
        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean write_desc = gatt.writeDescriptor(desc);
        Log.d("BLE", "Descriptor write: " +write_desc); // returns true

    }
    public void requestCharacteristics(BluetoothGatt gatt) {
        for(BluetoothGattCharacteristic test: chars){
            boolean temp = gatt.readCharacteristic(test);
            Log.d("TEST", "read characteristic: " + temp);
        }
    }

    private void fireDisconnected() {
        for (BLEControllerListener l : this.listeners)
            l.BLEControllerDisconnected();

        this.device = null;
    }

    private void fireConnected() {
        for (BLEControllerListener l : this.listeners)
            l.BLEControllerConnected();
    }

    private void fireDeviceFound(BluetoothDevice device) {
        for (BLEControllerListener l : this.listeners)
            l.BLEDeviceFound(device.getName().trim(), device.getAddress());
    }


    public void readData(byte[] data) {
        bluetoothGatt.readCharacteristic(mAcceleration);
        chars.add(btGattChar);
        Log.i("CHAR","btGattChar= "+btGattChar);
        requestCharacteristics(bluetoothGatt);
        setNotifySensor(bluetoothGatt);
        Log.i("GATT","bluetoothGatt= "+bluetoothGatt);
    }


    public void disconnect() {
        this.bluetoothGatt.disconnect();
    }
}


