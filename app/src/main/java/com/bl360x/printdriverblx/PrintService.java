package com.bl360x.printdriverblx;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PrintService extends Service {
    public PrintService() {
    }

    BroadcastReceiver bcReceiver = null;
    SharedPreferences sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("BLPrint","Service onBind Called!");
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BLPrint","Service On Start command Run");
        IntentFilter filter = new IntentFilter("com.bl360x.printdriver.NOTICE_TO_PRINT");
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        String pband = sharedPreferences.getString(getString(R.string.pref_brand),"NO");
        //TODO:Add new broadcast receivers here for new printer SDKs
        if(pband.equals("EPSON")){
            bcReceiver = new EPSONPrintReceiver();
        }else if(pband.equals("BIXOLON")){
            bcReceiver = new BIXOLONReceiver();
        }else{
            bcReceiver = new UnknownPrintReceiver();
        }
        registerReceiver(bcReceiver,filter);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent intentx = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);

        String CHANNEL_ID = getString(R.string.channel_id);
        Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("Printing Service Running!")
                .setContentText("Blue Lotus Apps can Print in your devices")
                .setSmallIcon(R.drawable.icon_main)
                .setContentIntent(intentx).build();
        startForeground(199806,notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("BLPrint","Service Destroyed!");
        unregisterReceiver(bcReceiver);
        super.onDestroy();
    }
}