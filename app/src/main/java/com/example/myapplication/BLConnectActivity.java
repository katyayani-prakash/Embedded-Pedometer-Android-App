package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.bluetooth.BluetoothProfile.GATT;

import com.google.android.material.snackbar.Snackbar;

public class BLConnectActivity extends AppCompatActivity implements BLEControllerListener{
    private TextView logView;
    private Button connectButton;
    private Button disconnectButton;
    //private Button switchLEDButton;

    private BLEController bleController;
    private RemoteControl remoteControl;
    private String deviceAddress;
    private String deviceName;

    private boolean isLEDOn = false;

    private boolean isAlive = false;
    private Thread heartBeatThread = null;
    private Toolbar toolbar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blconnect);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.bleController = BLEController.getInstance(this);
        this.remoteControl = new RemoteControl(this.bleController);

        this.logView = findViewById(R.id.logView);
        this.logView.setMovementMethod(new ScrollingMovementMethod());

        setSupportActionBar(toolbar2);
        toolbar2 = findViewById(R.id.toolbar2);

        initConnectButton();
        initDisconnectButton();
//        initSwitchLEDButton();

        checkBLESupport();
        checkPermissions();

        disableButtons();
    }

    public void startHeartBeat() {
        this.isAlive = true;
        this.heartBeatThread = createHeartBeatThread();
        this.heartBeatThread.start();
    }

    public void stopHeartBeat() {
        if(this.isAlive) {
            this.isAlive = false;
            this.heartBeatThread.interrupt();
        }
    }

    private Thread createHeartBeatThread() {
        return new Thread() {
            @Override
            public void run() {
                while(BLConnectActivity.this.isAlive) {
                    heartBeat();
                    try {
                        Thread.sleep(1000l);
                    }catch(InterruptedException ie) { return; }
                }
            }
        };
    }

    private void heartBeat() {
        this.remoteControl.heartbeat();
    }

    private void initConnectButton() {
        this.connectButton = findViewById(R.id.connectButton);
        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectButton.setEnabled(false);
                MainActivity.toolbar.setTitle("   Connecting BLE devices...");
                log("Connecting...");
                bleController.connectToDevice(deviceAddress);
            }
        });
    }

    private void initDisconnectButton() {
        this.disconnectButton = findViewById(R.id.disconnectButton);
        this.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectButton.setEnabled(false);
                MainActivity.toolbar.setTitle("   Finding BLE devices...");
                log("Disconnecting...");
                bleController.disconnect();
            }
        });
    }

//    private void initSwitchLEDButton() {
//        this.switchLEDButton = findViewById(R.id.switchButton);
//        this.switchLEDButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isLEDOn = !isLEDOn;
//                remoteControl.switchLED(isLEDOn);
//                log("LED switched " + (isLEDOn?"On":"Off"));
//            }
//        });
//    }

    private void disableButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(false);
                //switchLEDButton.setEnabled(false);
            }
        });
    }

    private void log(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logView.setText(logView.getText() + "\n" + text);
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            log("\"Access Fine Location\" permission not granted yet!");
            log("Without this permission Bluetooth devices cannot be searched!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    42);
        }
    }

    private void checkBLESupport() {
        // Check if BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.deviceAddress = null;
        this.bleController = BLEController.getInstance(this);
        this.bleController.addBLEControllerListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            log("[BLE]\tSearching for Thunderboard #61545...");
            this.bleController.init();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.bleController.removeBLEControllerListener(this);
        stopHeartBeat();
    }

    @Override
    public void BLEControllerConnected() {
        MainActivity.toolbar.setTitle("   ["+deviceName+"] connected");
        log("[BLE]\tConnected");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disconnectButton.setEnabled(true);
                //switchLEDButton.setEnabled(true);
            }
        });
        startHeartBeat();
    }

    @Override
    public void BLEControllerDisconnected() {
        MainActivity.toolbar.setTitle("   ["+deviceName+"] disconnected");
        log("[BLE]\tDisconnected");
        disableButtons();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setEnabled(true);
            }
        });
        //this.isLEDOn = false;
        stopHeartBeat();
    }

    @Override
    public void BLEDeviceFound(String name, String address) {
        log("Device " + name + " found with address " + address);
        this.deviceAddress = address;
        this.connectButton.setEnabled(true);
        this.deviceName = name;
        MainActivity.toolbar.setTitle("   Connecting ["+deviceName+"]...");
    }
}
