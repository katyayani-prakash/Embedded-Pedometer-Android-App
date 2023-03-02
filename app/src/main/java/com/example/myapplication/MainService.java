package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MainService extends Service implements SensorEventListener {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        if(MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
//            MainActivity.sensorManager.registerListener(this, MainActivity.sensorSD, SensorManager.SENSOR_DELAY_UI);
//        }else{
//            Toast.makeText(this, "sensorSD not found", Toast.LENGTH_SHORT).show();
//        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if(MainActivity.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
        //    MainActivity.sensorManager.unregisterListener(this, MainActivity.sensorSD);
        //}

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        if(sensorEvent.sensor == MainActivity.sensorSD){
//            MainActivity.stepDetect = (int) (MainActivity.stepDetect + sensorEvent.values[0]);
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
