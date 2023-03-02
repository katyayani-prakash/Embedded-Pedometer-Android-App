package com.example.myapplication;

public interface BLEControllerListener {
    public void BLEControllerConnected();
    public void BLEControllerDisconnected();
    public void BLEDeviceFound(String name, String address);
}