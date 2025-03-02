package ru.mipt.farmremotecontrolapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothConnectionService extends Service {
    public BluetoothConnectionService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}