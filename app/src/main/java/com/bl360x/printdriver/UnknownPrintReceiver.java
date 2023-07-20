package com.bl360x.printdriver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class UnknownPrintReceiver extends BroadcastReceiver {
    private String bluetoothMac;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME_BL360X", Context.MODE_PRIVATE);
        bluetoothMac = sharedPreferences.getString("PREF_BT_MAC", "");
        //String details = extras.getString("vals1");
        try{
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = adapter.getRemoteDevice(bluetoothMac);
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            BluetoothSocket btSocket = device.createRfcommSocketToServiceRecord(uuid);
            String base64Extra = extras.getString("details","");
            byte[] printingBytes = Base64.decode(base64Extra,Base64.DEFAULT);
            btSocket.connect();
            btSocket.getOutputStream().write(printingBytes);
            btSocket.close();
        }catch (Exception ex){
            Log.e("Unknown Printer ",ex.getMessage());
        }
    }
}