package com.bl360x.printdriver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIInteraction{
    public PrintDetails GetImages(String url,String companyKey,String orderId, String transactionTypeKey) throws MalformedURLException, IOException, JSONException {
        URL urls = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)urls.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Accept","application/json");
        connection.setRequestProperty("IntegrationID","1aa6a39b-5f54-4905-880a-a52733fd6105");
        connection.setDoInput(true);
        connection.setDoInput(true);

        JSONObject jobj = new JSONObject();
        jobj.put("companyKey",companyKey);
        jobj.put("OrderId",orderId);
        jobj.put("transactionTypeKey",transactionTypeKey);

        //JSONArray array = new JSONArray();

        /*if(parameters != null){
            String[] paramdivided = parameters.split(";");
            for (String pm : paramdivided){
                String[] pmd = pm.split(":");
                JSONObject obj1 = new JSONObject();
                obj1.put("param",pmd[0]);
                obj1.put("paramContent",pmd[1]);
                array.put(obj1);
            }
        }*/
        //JSONObject obj1 = new JSONObject();
//        for(KeyValuePair kvp : contents){
//            JSONObject obj1 = new JSONObject();
//            obj1.put("param",kvp.key);
//            obj1.put("paramContent",kvp.value);
//            array.put(obj1);
//        }
//        obj1.put("param","CKy");
//        obj1.put("paramContent",156);
//        JSONObject obj2 = new JSONObject();
//        obj2.put("param","UsrKy");
//        obj2.put("paramContent",343571);
//        JSONObject obj3 = new JSONObject();
//        obj3.put("param","UsrId");
//        obj3.put("paramContent","Gayantha.BL");
//        JSONObject obj4 = new JSONObject();
//        obj4.put("param","OrdKy");
//        obj4.put("paramContent",1230769);

//        array.put(obj1);
//        array.put(obj2);
//        array.put(obj3);
//        array.put(obj4);

        //jobj.put("ReportParams",array);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        dos.writeBytes(jobj.toString());
        dos.flush();
        dos.close();

        InputStream inputStream;
        if(connection.getResponseCode() == 200){
            inputStream = connection.getInputStream();
        }else return new PrintDetails();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1){
            outputStream.write(buffer,0,bytesRead);
        }
        byte[] responseBytes = outputStream.toByteArray();
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        outputStream.close();
        inputStream.close();
        String lineData;
        //StringBuffer sbfr = new StringBuffer();
//        while ((lineData = br.readLine())!=null){
//            sbfr.append(lineData);
//        }
        //JSONObject job = new JSONObject(responseString);
        //JSONArray dbta = job.getJSONArray("printData");
        PrintDetails pdds = new PrintDetails();
        pdds.printData = new ArrayList<String>();
//        for(int i = 0 ; i < dbta.length() ; i++){
//            pdds.printData.add(dbta.getString(i));
//        }
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(responseString);
        if(matcher.find()){
            String clean1 = matcher.group(1);
            String[] clean2 = clean1.split(",");
            for(String cleaned : clean2){
                pdds.printData.add(cleaned.substring(1,cleaned.length() -1));
            }
        }else return new PrintDetails();
        //PrintDetails response = (PrintDetails)connection.getContent();
        return pdds;
        //return new PrintDetails();
    }
}