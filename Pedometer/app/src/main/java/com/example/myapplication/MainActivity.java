package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivityLog";

    private TextView textViewStepCounter, textViewStepDetector;
    private SensorManager sensorManager;
    private Sensor sensorSC, sensorSD;
    private boolean isCounterSensorPresent, isDetectorSensorPresent;
    int stepCount = 0, stepDetect = 0;

    private SharedPreferences sharedPreferencesSave, sharedPreferencesLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        setSupportActionBar(binding.toolbar);

        //Toast message displays current state
        Toast.makeText(getApplicationContext(), "Create the first activity.", Toast.LENGTH_SHORT)
                .show();

        //Step Counter and Detector
            resetSteps();
            //display text
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            textViewStepCounter = findViewById(R.id.StepCounter);
            textViewStepDetector = findViewById(R.id.StepDetector);
             //sensor
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
                sensorSC = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

                isCounterSensorPresent = true;
            }else{
                textViewStepCounter.setText("Total steps: NA");
                isCounterSensorPresent = false;
            }

            if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!=null){
                sensorSD = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                isDetectorSensorPresent = true;
            }else{
                textViewStepDetector.setText("Step counter: NA");
                isDetectorSensorPresent = false;
            }


        //fragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //click message icon
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Message icon");
                Snackbar.make(view, "Good Morning!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        //Toast message displays current state
        Toast.makeText(getApplicationContext(), "Start the first activity.", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.d(TAG, "onResume");

        //StepCounter
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            sensorManager.registerListener(this, sensorSC, SensorManager.SENSOR_DELAY_UI);
        }else{
            Toast.makeText(this, "sensorSC not found", Toast.LENGTH_SHORT).show();
        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            sensorManager.registerListener(this, sensorSD, SensorManager.SENSOR_DELAY_UI);
        }else{
            Toast.makeText(this, "sensorSD not found", Toast.LENGTH_SHORT).show();
        }

        //Toast message displays current state
        Toast.makeText(getApplicationContext(), "Resume the first activity.", Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.d(TAG, "onPause");

        //StepCounter
        //if unregister the hardware will stop detecting steps
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            sensorManager.unregisterListener(this, sensorSC);
        }else{
            textViewStepCounter.setText("Total steps: onPause");
        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            sensorManager.unregisterListener(this, sensorSD);
        }else{
            textViewStepDetector.setText("Step counter: onPause");
        }

        //Toast message displays current state
        Toast.makeText(getApplicationContext(), "Pause the first activity.", Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        //Toast message displays current state
        Toast.makeText(getApplicationContext(), "Stop the first activity.", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        //Toast message displays current state
        Toast.makeText(getApplicationContext(), "Destroy the first activity.", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");

        //Toast message displays current state
        Toast.makeText(getApplicationContext(), "Restart the first activity.", Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Toast.makeText(getApplicationContext(), "You click share", Toast.LENGTH_SHORT)
                    .show();
        }else if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "You click settings", Toast.LENGTH_SHORT)
                    .show();
        }else if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "You click search", Toast.LENGTH_SHORT)
                   .show();
            Intent mIntent = new Intent(this, MainActivity2.class);
            startActivity(mIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "Going up a level from arrow on header bar");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor == sensorSC){
        //if(isCounterRunning){
            stepCount = (int) sensorEvent.values[0];
            textViewStepCounter.setText("Total steps: " + String.valueOf(stepCount));
        }
        else if(sensorEvent.sensor == sensorSD){
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);
            textViewStepDetector.setText("Step counter: " + String.valueOf(stepDetect));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void resetSteps() {
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepDetect = 0;
                textViewStepDetector.setText("Step counter: " + String.valueOf(stepDetect));
            }
        });

    }

}