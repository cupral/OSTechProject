package com.honda.mdve.sampleapplication;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ParseJsonData {
    private static final String TAG = "Sample_parseJsonData";

    private static final String[] currentaudio_Data = new String[]{
            "source",
            "shufflemode",
            "repeatmode",
            "volume",
            "strdata1",
            "strdata2",
            "strdata3",
            "strdata4",
            "strdata5",
            "strdata6",
            "strdata7",
            "strdata8",
            "strdata9",
            "strdata10",
            "strdata11",
            "strdata12",
            "numdata1",
            "numdata2"  };




    public interface Token{
        int setToken(ArrayList<String> string);
    }

    public void parseJSON_Vehcledata(String jsonstring, Token token){

        JSONObject obj_result = null;
        String result = "";
        String code = "";
        // string length check
        if(jsonstring.length() < "{\"result\":\"Error\"}".length()){
            return;
        }
        // result check
        try {
            obj_result = new JSONObject(jsonstring);
            result = obj_result.getString("result");
            code = obj_result.getString("code");
        }catch(JSONException e){
            ;
        }
        ArrayList<String> string = new ArrayList<String>();
        string.add("code");
        string.add("0");
        string.add(code);
        token.setToken(string);
        if( result.equals("Error")){
            return;
        }
        Iterator iterator = obj_result.keys();
        while(iterator.hasNext()){
            String name = (String)iterator.next();
            if(name.equals("code")) continue;
            if(name.equals("result")) continue;

            // Vehcledata check Tag
            JSONObject common_data = null;
            try {
                String getdata_str = obj_result.get(name).toString();
                common_data = new JSONObject(getdata_str);
            }catch(JSONException e){
                e.printStackTrace();
            }
            if(common_data != null) {
                String status = null;
                String data = null;
                String currentaudioData = null;
                if( name.equals("current_audio") == true ){
                    try {
                        status = common_data.getString("status").toString();
                        if( status.equals("0") == true ){
                            string.set(0, "current_audio");
                            string.set(1, "0");
                            string.set(2, "");
                            token.setToken(string);
                        }
                    } catch (JSONException e) {
                        ;
                    }
                    for( String Key : currentaudio_Data ) {
                        try {
                            currentaudioData = common_data.getString(Key).toString();
                            string.set(0, "current_audio." + Key);
                            string.set(1, status);
                            string.set(2, currentaudioData);
                            token.setToken(string);
                        } catch (JSONException e) {
                            ;
                        }
                    }
                }
                else {
                    // Vehcledata read status/data
                    try {
                        status = common_data.getString("status").toString();
                        data = common_data.getString("data").toString();
                    } catch (JSONException e) {
                        ;
                    }
                    string.set(0, name);
                    string.set(1, status);
                    string.set(2, data);
                    token.setToken(string);
                }
            }
        }
    }

    public void parseJSON_Responsedata(String jsonstring, Token token){
        JSONObject  obj_result     = null;
        String      result         = "";
        String      dataString[]   = new String[4];
        String      code           = "-";
        String      command        = "-";
        String      count          = "-";
        String      audiosource    = "-";
        // string length check　
          if(jsonstring.length() < "{\"result\":\"Error\"}".length()){
            Log.d(TAG,"parseJSON_Responsedata: length Error! length:" + String.valueOf(jsonstring.length()));
            return;
        }

        // result check
        try {
            obj_result = new JSONObject(jsonstring);
            result  = obj_result.getString("result");
            code    = obj_result.getString("code");
            command  = obj_result.getString("command");
            count    = obj_result.getString("count");
        }catch(JSONException e){
            Log.d(TAG,"parseJSON_Responsedata: Not Found Common Data! result:" + result +  " code:" + code + " command:" + command + " count:" + count);
            e.printStackTrace();
        }
        ResponseData.setCode(code);
        ResponseData.setCount(count);
        ResponseData.setCommand(command);

        if( result.equals("Error")){
            return;
        }
        if( code.equals("0") == false) {
            return;
        }
        Iterator iterator = obj_result.keys();
        while(iterator.hasNext()) {
            String name = (String) iterator.next();
            if (name.equals("result")) continue;
            if (name.equals("code")) continue;
            if (name.equals("command")) continue;
            if (name.equals("count")) continue;
            // ResponseData check Tag
            JSONObject common_data = null;
            int dataSum = Integer.parseInt(count);
            String getdata_str="";
            if (dataSum != 0) {
                for (int i = 0;i < dataSum; i++) {
                    if( name.equals("no1")) {
                        try {
                            audiosource = obj_result.getJSONObject("no1").getString("data1");
                            ResponseData.setAudiosource(audiosource);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            getdata_str = obj_result.get(name).toString();
                            common_data = new JSONObject(getdata_str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (common_data != null) {
                            int dataNum;
                            ArrayList<String> string = new ArrayList<String>();
                            string.add(name);
                            string.add(getdata_str);
                            // Read     data1, data2, data3, ... , dataXX
                            for(  dataNum = 0 ; ; dataNum++ ) {
                                try {
                                    String data = common_data.getString("data" + (dataNum + 1)).toString();
                                    string.add(data);
                                } catch (JSONException e) {
                                    break;
                                }
                            }
                            token.setToken(string);
                        }
                    }
                }
            }
        }
    }
}

