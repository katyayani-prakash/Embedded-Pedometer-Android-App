package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
//import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.bluetooth.BluetoothAdapter;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivityLog";

//    public static SensorManager sensorManager;
//    public static Sensor sensorSD;
//    public static int stepDetect = 0;

    public static ProgressBar progressBar;
    public static TextView progressText;
    private TextView goals;
    public static Toolbar toolbar;
    private static boolean goalMet;

    //BLE
    private boolean scanning;

    private SharedPreferences sharedPreferencesSave, sharedPreferencesLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        setSupportActionBar(binding.toolbar);
        toolbar = findViewById(R.id.toolbar);

        //ble
        scanning = true;
        checkBLESupport();
        checkPermissions();
        // toolbar.setTitle("   Finding BLE devices...");

        resetSteps();
        goalsMet();

        //Progress bar
        progressText = findViewById(R.id.progress_text);
        progressText.setText(BLEController.HalfStepCounter/2 + " steps completed");
        Log.d("MainActiVity", "Pedometer steps : "+BLEController.HalfStepCounter/2);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(BLEController.HalfStepCounter/2);
        //defining the daily goals
        goals = findViewById(R.id.goals_display);

//        //Step Detector
//            //display text
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            //sensor
//            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//            if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!=null){
//                sensorSD = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//            }else{
//                progressText.setText(stepDetect+" steps completed");
//            }

        //fragment
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

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
    protected void onResume() {

        super.onResume();
        //Log.d(TAG, "onResume");

//        //StepCounter
//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
//            sensorManager.registerListener(this, sensorSD, SensorManager.SENSOR_DELAY_UI);
//        }else{
//            Toast.makeText(this, "sensorSD not found", Toast.LENGTH_SHORT).show();
//        }
//        //Display step goals
        goals.setText("Your Daily Step Goal = " +SecondActivity.goalInput);
        progressBar.setMax(SecondActivity.goalInput);
        progressText.setText(BLEController.HalfStepCounter/2 + " steps completed");
        progressBar.setProgress(BLEController.HalfStepCounter/2);

    }

    @Override
    protected void onPause() {

        super.onPause();
       // Log.d(TAG, "onPause");

        //StepCounter
        //if unregister the hardware will stop detecting steps
        //if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
        //    sensorManager.unregisterListener(this, sensorSD);
        //}

    }

    @Override
    protected void onStop() {
        super.onStop();
       // Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(TAG, "onRestart");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!scanning){
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }else{
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "You clicked search", Toast.LENGTH_SHORT).show();
            Intent mIntent = new Intent(this, BLConnectActivity.class);
            startActivity(mIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user touches the button */
    public void startSecondActivity(View view) {
        // Start Second Activity in response to button click
        Intent mIntent = new Intent(this, SecondActivity.class);
        startActivity(mIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//
//        if(sensorEvent.sensor == sensorSD){
//            stepDetect = (int) (stepDetect + sensorEvent.values[0]);
//            progressText.setText(stepDetect + " steps completed");
//            progressBar.setProgress(stepDetect);
//        }
//        goalsMet();
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }


    public void resetSteps() {
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BLEController.HalfStepCounter = 0;
                progressText.setText(BLEController.HalfStepCounter/2 + " steps completed");
                progressBar.setProgress(BLEController.HalfStepCounter/2);
            }
        });

    }
    public void goalsMet() {
        if((BLEController.HalfStepCounter/2 == SecondActivity.goalInput)&&(SecondActivity.goalInput != 0)){
            Toast.makeText(this, "Congratulations! You've met your daily goal!", Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Lack of Permissions for BLE!", Toast.LENGTH_SHORT).show();
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
}