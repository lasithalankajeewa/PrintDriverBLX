package com.bl360x.printdriverblx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.io.InputStream;
import java.net.URL;

public class EPSONPrintReceiver extends BroadcastReceiver implements ReceiveListener {

    private static Printer mPrinter = null;
    private Context mContext = null;
    private int printerModel = -1;
    private String bluetoothMac = "";
    private int DISCONNECT_INTERVAL = 500;

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
        //List<KeyValuePair> parameters = extras.getParcelableArrayList("parameters");
        Data data = new Data.Builder().putString("companyKey",companyKey).putString("companyKey",orderId).putString("transactionTypeKey",transactionTypeKey).putString("token",token).build();
        WorkRequest printRequest = new OneTimeWorkRequest.Builder(EPSONPrintWorker.class).setInputData(data).build();
        WorkManager.getInstance(context).enqueue(printRequest);

//            mContext = context;
//
//        if(!initializeObject()){
//            return;
//        }
//
//        if(!connectPrinter()){
//            return;
//        }
//
//
//        try{
//            if(dataType.equals("PDF")){
//                String details = extras.getString("details");
//                addPDFFeed(details);
//            }else if(dataType.equals("BMP")){
//                byte[] details = extras.getByteArray("details");
//                runnable.run();
//            }
//            mPrinter.addCut(Printer.CUT_FEED);
//            implementPrint();
//        }catch (Exception ex){
//            Log.e("BCRes",ex.getMessage());
//        }
//
//        Log.d("Printer","Printing Success");
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                addBMPFeed();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private void addBMPFeed() throws Exception{
        URL uri = new URL("https://bluelotus360.co/3pl/SGF/Cat/Sun Maid.png");
        InputStream inputStream = uri.openStream();
        Bitmap bmp =BitmapFactory.decodeStream(inputStream);
        mPrinter.addImage(bmp,0,0,bmp.getWidth(),bmp.getHeight(),Printer.PARAM_DEFAULT,Printer.MODE_MONO,Printer.HALFTONE_THRESHOLD,Printer.PARAM_DEFAULT,Printer.PARAM_DEFAULT);
    }
    private void addPDFFeed(String details) throws Exception{
        //byte[] pdfBytes = Base64.decode(details,Base64.DEFAULT);
        //String utf8String = new String(pdfBytes, StandardCharsets.UTF_8);
        //mPrinter.addSymbol("0111234",Printer.SYMBOL_PDF417_STANDARD,Printer.PARAM_DEFAULT,3,3,0);
        //mPrinter.add
        mPrinter.addCut(Printer.CUT_FEED);
    }

    private boolean implementPrint(){
        if(mPrinter == null){
            return false;
        }
//        if(!connectPrinter()){
//            mPrinter.clearCommandBuffer();
//            return false;
//        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            mPrinter.clearCommandBuffer();
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        return true;
    }

    private boolean connectPrinter(){
        if (mPrinter == null) {
            return false;
        }

        try {
            if(!bluetoothMac.equals("")){
                mPrinter.connect("BT:"+bluetoothMac, Printer.PARAM_DEFAULT);
            }else{
                return false;
            }
        }
        catch (Exception e) {
            //Toast.makeText(this, "Connection Issue Got", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            if(printerModel > -1){
                mPrinter = new Printer(printerModel,Printer.MODEL_ANK,mContext);
            }else{
                return false;
            }
        }
        catch (Exception e) {
            //Toast.makeText(this, "Error Ctreating Object", Toast.LENGTH_SHORT).show();
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private void disconnectPrinter(){
        if (mPrinter == null) {
            return;
        }

        while (true) {
            try {
                mPrinter.disconnect();
                break;
            } catch (final Exception e) {
            }
        }

        mPrinter.clearCommandBuffer();
    }

    @Override
    public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                disconnectPrinter();
            }
        }).start();
    }
}