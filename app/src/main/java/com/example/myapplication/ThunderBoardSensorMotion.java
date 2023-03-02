package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.SystemClock;

import android.os.Bundle;

public class ThunderBoardSensorMotion extends ThunderBoardSensor {

    private int characteristicsStatus;

    private SensorData sensorData = new SensorData();

    // CSC Feature
    private boolean wheelRebolutionDataSupported;
    // CSC Measurements
    private boolean wheelRevolutionDataPresent;
    private int cumulativeWheelRevolutions;
    private int lastWheelRevolutionTime; // seconds
    private int rotationsPerMinute;
    private long csrMeasurementEvent;

    @Override
    public ThunderboardSensorData getSensorData() {
        return sensorData;
    }

    public int getCharacteristicsStatus() {
        return characteristicsStatus;
    }

    public void clearCharacteristicsStatus() {
        characteristicsStatus = 0;
    }

    public void setAccelerationNotificationEnabled(boolean enabled) {
        characteristicsStatus |= 0x30;
    }

    public void setAcceleration(float accelerationX, float accelerationY, float accelerationZ) {
        sensorData.ax = accelerationX;
        sensorData.ay = accelerationY;
        sensorData.az = accelerationZ;
        isSensorDataChanged = true;
    }

    public static class SensorData implements ThunderboardSensorData {
        @Override
        public String toString() {
            return String.format("%f %f %f %f %f %f", ax, ay, az, ox, oy, oz);
        }

        // Acceleration along X-axis in . Units in g with resolution of 0.001 g
        public float ax;
        // Acceleration along Y-axis in . Units in g with resolution of 0.001 g
        public float ay;
        // Acceleration along Z-axis in . Units in g with resolution of 0.001 g
        public float az;
        // Orientation alpha angle in deg (+180 to -180) with resolution of 0.01 deg
        public float ox;
        // Orientation beta angle in deg (+90 to -90) with resolution of 0.01 deg
        public float oy;
        // Orientation gamma angle in deg (+180 to -180) with resolution of 0.01 deg
        public float oz;

        // CSC
        public double speed;
        public double distance;

        @Override
        public ThunderboardSensorData clone() {
            SensorData d = new SensorData();

            d.ax = ax;
            d.ay = ay;
            d.az = az;
            d.ox = ox;
            d.oy = oy;
            d.oz = oz;
            d.distance = distance;
            d.speed = speed;

            return d;
        }
    }
}