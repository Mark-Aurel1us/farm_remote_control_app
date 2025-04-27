package ru.mipt.farmremotecontrolapp;

import static java.util.Objects.isNull;

import static ru.mipt.farmremotecontrolapp.Utils.pathFromIntent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.*;

public class FarmConfig {
    private static final String TAG = "FarmConfig";
    //TODO: objectify
    static final String[] PROPERTIES_NAMES = {
            "pump_interval_days",
            "pump_start_hours",
            "pump_start_minutes",
            "pump_volume_ml",
            "heatlamp_target_temp",
            "growlight_on_hours",
            "growlight_on_minutes",
            "growlight_off_hours",
            "growlight_off_minutes",
    };
    static final int[] PROPERTIES_MIN = {0,0,0,0,0,0,0,0,0};
    static final int[] PROPERTIES_MAX = {100,100,100,100,100,100,100,100,100};
    static final int PROPERTIES_NUMBER = PROPERTIES_NAMES.length;

    public class Property {
        public Property(JSONObject jsonObject){
        }

        public JSONObject toJSONObject(){
            return null;
        }

    }

    public class Actuator {

    }

    private int[] properties;

    private FarmConfig(int[] array){
        if(isNull(array) || array.length != PROPERTIES_NUMBER){
            this.properties = PROPERTIES_MIN.clone();
        }
        else {
            this.properties = new int[PROPERTIES_NUMBER];
            for(int i = 0; i < PROPERTIES_NUMBER; i ++){
                this.setProperty(i, array[i]);
            }

        }
    }

    public JSONObject toJSONObject(){
        try {
            JSONObject jsonObject = new JSONObject();
            for(int i = 0; i < PROPERTIES_NUMBER; i ++){
                jsonObject.put(PROPERTIES_NAMES[i], properties[i]);
            }
            return jsonObject;
        } catch (JSONException e){
            Log.d(TAG, e.getMessage());
            return null;
        }
    }


    public static FarmConfig fromFile(Path path){
        if(isNull(path)){return new FarmConfig(null);}
        try {
            File f = path.toFile();
            String pathString = f.getAbsolutePath().replace("/document/raw:", "");
            f = new File(pathString);
            FileReader fileReader = new FileReader(f);
            Scanner in = new Scanner(fileReader);
            StringBuilder sb = new StringBuilder();
            while(in.hasNext()) {
                sb.append(in.next());
            }
            in.close();
            String contents = sb.toString();
            JSONObject json = new JSONObject(contents);
            int[] parsed_properties = new int[FarmConfig.PROPERTIES_NUMBER];
            for(int i = 0; i < FarmConfig.PROPERTIES_NUMBER; i ++ ){
                parsed_properties[i] = json.getInt(FarmConfig.PROPERTIES_NAMES[i]);
                Log.d(TAG, "Got integer " + parsed_properties[i] + " for property " + FarmConfig.PROPERTIES_NAMES[i]);
            }
            return new FarmConfig(parsed_properties);

        } catch (IOException e) {
            Log.d(TAG,"Error reading JSON file: " + e.getMessage());
        } catch (JSONException e) {
            Log.d(TAG,"Error parsing JSON file");
        }
        return new FarmConfig(null);
    }

    public byte[] toByteArray(){
        ByteBuffer b = ByteBuffer.allocate(4 * PROPERTIES_NUMBER);
        for(int i = 0; i < PROPERTIES_NUMBER; i ++){
            b = b.putInt(properties[i]);
        }
        return b.array();

    }
/*
    public static FarmConfig fromByteArray(byte[] configBytes) {
        if(isNull(configBytes)) return null;
        if(configBytes.length != 4 * PROPERTIES_NUMBER){return null;}
        ByteBuffer b = ByteBuffer.wrap(configBytes);
        int[] array = new int[PROPERTIES_NUMBER];
        for(int i = 0; i < PROPERTIES_NUMBER; i ++){
            array[i] = b.getInt(configBytes[i]);
        }
        return new FarmConfig(array);

    }*/

//    public static FarmConfig fromIntent (Intent intent){
//        return fromFile(pathFromIntent(intent));
//    }

    public int getProperty(int n){
        if(isNull(properties) || n >= PROPERTIES_NUMBER){return 0;}
        if(properties[n] < FarmConfig.PROPERTIES_MIN[n]){return FarmConfig.PROPERTIES_MIN[n];}
        if(properties[n] > FarmConfig.PROPERTIES_MAX[n]){return FarmConfig.PROPERTIES_MAX[n];}
        return properties[n];
    }

    public void setProperty(int n, int value){
        if(value < FarmConfig.PROPERTIES_MIN[n]){value = FarmConfig.PROPERTIES_MIN[n];}
        if(value > FarmConfig.PROPERTIES_MAX[n]){value = FarmConfig.PROPERTIES_MAX[n];}
        this.properties[n] = value;
    }
}
