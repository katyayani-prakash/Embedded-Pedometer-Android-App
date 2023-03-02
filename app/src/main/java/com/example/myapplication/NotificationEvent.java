package com.example.myapplication;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class NotificationEvent {

    public final static int ACTION_NOTIFICATIONS_CLEAR = 1;
    public final static int ACTION_NOTIFICATIONS_SET = 2;

    public final BluetoothDevice device;
    public final UUID characteristicUuid;
    public final int action;

    public NotificationEvent(BluetoothDevice device, UUID characteristicUuid, int action) {
        this.device = device;
        this.action = action;
        this.characteristicUuid = characteristicUuid;
    }
}