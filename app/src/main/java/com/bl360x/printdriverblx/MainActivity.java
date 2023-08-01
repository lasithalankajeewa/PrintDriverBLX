package com.bl360x.printdriverblx;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bxl.config.editor.BXLConfigLoader;
import com.epson.epos2.printer.Printer;

public class MainActivity extends AppCompatActivity {

    private Button btn,stpBtn;

    private int REQUEST_CODE = 1001;
    private Spinner modelSpinner,brandSpinner;

    private TextView tv6;
    private EditText btMacText;
    private String brandName,prevBrand;
    private int prevModel;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String id = getString(R.string.channel_id);
            CharSequence name = getString(R.string.channel_name);
            String description = "Hello this is blue lotus printing channel notification";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(id,name,importance);
            channel.setDescription(description);
            NotificationManager Notificationmanagwr = getSystemService(NotificationManager.class);
            Notificationmanagwr.createNotificationChannel(channel);
        }

        RequestPermissions();

        modelSpinner = findViewById(R.id.spinner4);
        brandSpinner = findViewById(R.id.spinner3);
        btMacText = findViewById(R.id.editTextText);
        modelSpinner.setVisibility(View.INVISIBLE);
        tv6 = findViewById(R.id.textView6);

        sharedPreferences = this.getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        selectPrevious();

        String prevMac = sharedPreferences.getString(getString(R.string.pref_btmac),"");
        btMacText.setText(prevMac);

        String prevBrand = sharedPreferences.getString(getString(R.string.pref_brand),"");

        ArrayAdapter<String> brandAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        brandAdapter.add("EPSON");
        brandAdapter.add("BIXOLON");
        brandAdapter.add("Not In List");
        brandSpinner.setAdapter(brandAdapter);
        if(prevBrand.equals("EPSON")){
            brandSpinner.setSelection(0);
        }else if(prevBrand.equals("BIXOLON")){
            brandSpinner.setSelection(1);
        }else{
            brandSpinner.setSelection(2);
        }
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    saveSelectedBrand();
                    LoadModelData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServiceNow();
            }
        });


        stpBtn = findViewById(R.id.stp_srvce);
        stpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopServiceNow();
            }
        });
    }
    private void StopServiceNow(){
        Intent intent = new Intent(getApplicationContext(), PrintService.class);
        stopService(intent);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted->{
        if(isGranted){
            Log.d("Permission","Granted");
        }else{
            Log.d("permission","not granted");
        }
    });

    private void RequestPermissions(){
        String[] permissionList;

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
            permissionList = new  String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
        }else if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
//                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH);
//            }
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
//                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN);
//            }
            permissionList = new  String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
        }else{
            permissionList = new  String[]{
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
        }
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED){
//            requestPermissionLauncher.launch(Manifest.permission.FOREGROUND_SERVICE);
//        }
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
//            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//        }
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED){
//            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN);
//        }
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
//            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
//        }



        //int REQUEST_CODE = 1001;

        ActivityCompat.requestPermissions(this,permissionList,REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied
                    //RequestPermissions();
                }
            }
        }
    }

    private void startServiceNow(){
        sharedPreferencesEditor.putString(getString(R.string.pref_btmac),btMacText.getText().toString());
        sharedPreferencesEditor.apply();
        Intent intent = new Intent(getApplicationContext(), PrintService.class);
        startForegroundService(intent);
    }

    private void selectPrevious(){
        prevBrand = sharedPreferences.getString(getString(R.string.pref_brand),"Not added");
        prevModel = sharedPreferences.getInt(getString(R.string.pref_model),-1);
        tv6.setText(prevBrand + " "+ prevModel);
    }

    private void LoadModelData(){
        //TODO:Add models here to add new printer models provided by SDKs
        if(brandName == "EPSON"){
            modelSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter<SpnModelsItem> seriesAdapter = new ArrayAdapter<SpnModelsItem>(this, android.R.layout.simple_spinner_item);
            seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m10), Printer.TM_M10));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30), Printer.TM_M30));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p20), Printer.TM_P20));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60), Printer.TM_P60));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60ii), Printer.TM_P60II));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p80), Printer.TM_P80));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t20), Printer.TM_T20));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t60), Printer.TM_T60));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t70), Printer.TM_T70));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t81), Printer.TM_T81));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t82), Printer.TM_T82));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t83), Printer.TM_T83));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t83iii), Printer.TM_T83III));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t88), Printer.TM_T88));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90), Printer.TM_T90));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90kp), Printer.TM_T90KP));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t100), Printer.TM_T100));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u220), Printer.TM_U220));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u330), Printer.TM_U330));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l90), Printer.TM_L90));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_h6000), Printer.TM_H6000));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30ii), Printer.TM_M30II));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_ts100), Printer.TS_100));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m50), Printer.TM_M50));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t88vii), Printer.TM_T88VII));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l90lfc), Printer.TM_L90LFC));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_eu_m30), Printer.EU_M30));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l100), Printer.TM_L100));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p20ii), Printer.TM_P20II));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p80ii), Printer.TM_P80II));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30iii), Printer.TM_M30III));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m50ii), Printer.TM_M50II));
            seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m55), Printer.TM_M55));
            modelSpinner.setAdapter(seriesAdapter);
            for(int i = 0; i < seriesAdapter.getCount(); i++){
                SpnModelsItem item = seriesAdapter.getItem(i);
                int valconst = item.getModelConstant();
                if(prevModel == valconst){
                    modelSpinner.setSelection(i);
                }
            }
            modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    saveSelectedModel();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else if(brandName == "BIXOLON"){
            modelSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> seriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seriesAdapter.add(BXLConfigLoader.PRODUCT_NAME_SPP_R210);
            seriesAdapter.add(BXLConfigLoader.PRODUCT_NAME_SPP_100II);
            seriesAdapter.add(BXLConfigLoader.PRODUCT_NAME_SPP_C200);
            seriesAdapter.add(BXLConfigLoader.PRODUCT_NAME_SPP_R215);
            seriesAdapter.add(BXLConfigLoader.PRODUCT_NAME_SPP_R310);
            modelSpinner.setAdapter(seriesAdapter);
            modelSpinner.setSelection(1);
            modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    saveSelectedModel();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            return;
        }
    }

    private void saveSelectedModel(){

        if(brandName == "BIXOLON"){
            String data = (String)modelSpinner.getSelectedItem();
            sharedPreferencesEditor.putString("PREF_BX_MODEL",data);
            sharedPreferencesEditor.apply();
        }else {
            SpnModelsItem smi = (SpnModelsItem) modelSpinner.getSelectedItem();
            int c = smi.getModelConstant();
            sharedPreferencesEditor.putInt(getString(R.string.pref_model), c);
            sharedPreferencesEditor.apply();
        }
    }
    private void saveSelectedBrand(){
        brandName = (String)brandSpinner.getSelectedItem();
        sharedPreferencesEditor.putString(getString(R.string.pref_brand),brandName);
        sharedPreferencesEditor.apply();
    }
}