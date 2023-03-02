package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2Log";

    private int onCreateCounter = 0;
    private int onStartCounter = 0;
    private int onResumeCounter = 0;
    private int onPauseCounter = 0;
    private int onStopCounter = 0;
    private int onDestroyCounter = 0;
    private int onRestartCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        onCreateCounter = onCreateCounter + 1;
        TextView displayCounter = findViewById(R.id.TextView1);
        String temp = String.valueOf(onCreateCounter);
        StringBuffer temp1 = new StringBuffer("OnCreate: " + temp);
        displayCounter.setText(temp1);

        Toast.makeText(getApplicationContext(), "Create the second activity.", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        onStartCounter = onStartCounter + 1;
        TextView displayCounter = findViewById(R.id.TextView2);
        String temp = String.valueOf(onStartCounter);
        StringBuffer temp1 = new StringBuffer("OnStart: " + temp);
        displayCounter.setText(temp1);

        Toast.makeText(getApplicationContext(), "Start the second activity.", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.d(TAG, "onResume");

        onResumeCounter = onResumeCounter + 1;
        TextView displayCounter = findViewById(R.id.TextView3);
        String temp = String.valueOf(onResumeCounter);
        StringBuffer temp1 = new StringBuffer("OnResume: " + temp);
        displayCounter.setText(temp1);

        Toast.makeText(getApplicationContext(), "Resume the second activity.", Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.d(TAG, "onPause");

        onPauseCounter = onPauseCounter + 1;
        TextView displayCounter = findViewById(R.id.TextView4);
        String temp = String.valueOf(onPauseCounter);
        StringBuffer temp1 = new StringBuffer("OnPause: " + temp);
        displayCounter.setText(temp1);

        Toast.makeText(getApplicationContext(), "Pause the second activity.", Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        onStopCounter = onStopCounter + 1;
        TextView displayCounter = findViewById(R.id.TextView5);
        String temp = String.valueOf(onStopCounter);
        StringBuffer temp1 = new StringBuffer("OnStop: " + temp);
        displayCounter.setText(temp1);

        Toast.makeText(getApplicationContext(), "Stop the second activity.", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        onCreateCounter = 0;
        onStartCounter = 0;
        onResumeCounter = 0;
        onPauseCounter = 0;
        onStopCounter = 0;
        onRestartCounter = 0;

        onDestroyCounter = onDestroyCounter + 1;
        TextView displayCounter = findViewById(R.id.TextView7);
        String temp = String.valueOf(onDestroyCounter);
        StringBuffer temp1 = new StringBuffer("OnDestroy: " + temp);
        displayCounter.setText(temp1);

        Toast.makeText(getApplicationContext(), "Destroy the second activity.", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");

        onRestartCounter = onRestartCounter + 1;
        TextView displayCounter = findViewById(R.id.TextView6);
        String temp = String.valueOf(onRestartCounter);
        StringBuffer temp1 = new StringBuffer("OnRestart: " + temp);
        displayCounter.setText(temp1);

        Toast.makeText(getApplicationContext(), "Restart the Second activity.", Toast.LENGTH_SHORT)
                .show();
    }
}