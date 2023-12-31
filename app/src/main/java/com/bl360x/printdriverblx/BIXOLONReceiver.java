package com.bl360x.printdriverblx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class BIXOLONReceiver extends BroadcastReceiver {
    private String bluetoothMac = "";
    private int printerModel = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME_BL360X",Context.MODE_PRIVATE);
        bluetoothMac = sharedPreferences.getString("PREF_BT_MAC","");
        printerModel = sharedPreferences.getInt("PREF_MODEL",-1);


        //String dataType = extras.getString("dataType");
        String companyKey = extras.getString("companyKey");
        String orderId = extras.getString("orderId");
        String transactionTypeKey = extras.getString("transactionTypeKey");
        String token = extras.getString("token");

        //List<KeyValuePair> parameters = extras.getParcelableArrayList("Parameters");
        String url = extras.getString("URL");
        boolean isReport = extras.getBoolean("IsReport");
        String parameters = extras.getString("Parameters");
        Data data = new Data.Builder().putString("companyKey",companyKey).putString("orderId",orderId).putString("URL",url).putString("transactionTypeKey",transactionTypeKey).putString("Parameters",parameters).putBoolean("IsReport",isReport).putString("URL",url).putString("token",token).build();
        //Data data = new Data.Builder().putString("URL",url).putString("report",report).putString("parameters",parameters).putString("token",token).build();

        WorkRequest printRequest = new OneTimeWorkRequest.Builder(BIXOLONWorker.class).setInputData(data).build();
        WorkManager.getInstance(context).enqueue(printRequest);


    }
}