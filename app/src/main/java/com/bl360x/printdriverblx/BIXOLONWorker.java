package com.bl360x.printdriverblx;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.nio.ByteBuffer;
import java.util.List;

import com.bxl.config.editor.BXLConfigLoader;

import jpos.POSPrinter;
import jpos.POSPrinterConst;

public class BIXOLONWorker extends Worker {
    private String bluetoothMac = "",url = "",report = "",parameters = "";

    private boolean isReport=false;

    private String companyKey = "",orderId = "",transactionTypeKey = "",token="";
    private String printerModel = "";
    private APIInteraction apiInteraction;

    private POSPrinter posPrinter;
    private BXLConfigLoader bxlConfigLoader;
    private Context mContext;
    public BIXOLONWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME_BL360X",Context.MODE_PRIVATE);
        bluetoothMac = sharedPreferences.getString("PREF_BT_MAC","");
        printerModel = sharedPreferences.getString("PREF_BX_MODEL","");
        apiInteraction = new APIInteraction();

        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        //url = "https://bluelotusx.co/apiv2/api/Order/PrintOrderByOrderIdImage";
        //report = data.getString("Report");
        url = data.getString("URL");
        companyKey = data.getString("companyKey");
        orderId = data.getString("orderId");
        transactionTypeKey = data.getString("transactionTypeKey");
        token=data.getString("token");
        parameters=data.getString("Parameters");
        isReport=data.getBoolean("IsReport",false);
        try{
            PrintDetails pds = apiInteraction.GetImages(url,companyKey,orderId,transactionTypeKey,token,parameters,isReport);

            //Data datad = new Data.Builder().putStringArray("datas",pds.getImageList().toArray(new String[0])).build();
            //return Result.success(datad);
            ConfigurePrinter();
            OpenConnection();
            PrintImage(pds.printData);
            return Result.success();
        }catch (Exception ex){
            return Result.failure();
            //Data dat = new Data.Builder().putString("message",ex.getMessage()).build();
            //return Result.failure(dat);
        }finally {
                try{
                    posPrinter.close();
                }catch (Exception ex){
                    Log.d("error","error in dicsconnection");
                }
        }
    }

    public void ConfigurePrinter()throws Exception{
        bxlConfigLoader = new BXLConfigLoader(mContext);
        try{
            bxlConfigLoader.openFile();
        }catch (Exception ex){
            bxlConfigLoader.newFile();

        }
        try{
            bxlConfigLoader.addEntry("SPP-R200",BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER,printerModel,BXLConfigLoader.DEVICE_BUS_BLUETOOTH,bluetoothMac);

            bxlConfigLoader.saveFile();
        }catch (Exception ex){
            //
        }

    }

    public void OpenConnection() throws Exception{
        posPrinter = new POSPrinter(mContext);
        posPrinter.open("SPP-R200");
        posPrinter.claim(5000);
        posPrinter.setDeviceEnabled(true);
    }

    public void PrintImage(List<String> images) throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put((byte) POSPrinterConst.PTR_S_RECEIPT);
        buffer.put((byte) 0);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x01);

        for(String image : images){
            byte[] byteBee = Base64.decode(image,Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteBee,0,byteBee.length);
            posPrinter.printBitmap(buffer.getInt(0),bmp,1200,POSPrinterConst.PTR_BM_LEFT);

        }
    }
}
