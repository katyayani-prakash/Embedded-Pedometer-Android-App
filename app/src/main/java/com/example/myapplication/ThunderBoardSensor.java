package com.example.myapplication;

public abstract class ThunderBoardSensor {
    public Boolean isNotificationEnabled;
    public Boolean isSensorDataChanged = false;

    public abstract ThunderboardSensorData getSensorData();
}