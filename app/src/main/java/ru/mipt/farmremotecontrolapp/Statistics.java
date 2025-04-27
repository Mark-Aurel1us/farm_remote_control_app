package ru.mipt.farmremotecontrolapp;

import static java.util.Objects.isNull;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class Statistics {
    public static String TAG = Statistics.class.getName();

    /*
    "temperature_DHT22",
    "temperature_DS18B20",
    "humidity",
    "water_level",
    "soil_moisture",
    "double light_intensity"
    */
    static final String[] STATISTICS_NAMES = {
            "temperature_DHT22",
            "temperature_DS18B20",
            "humidity",
            "water_level",
            "soil_moisture",
            "double light_intensity"
    };
    static final int STATISTICS_COUNT = STATISTICS_NAMES.length;
    private double[][] data;
    private long[] time;
    private int length;
/*
    public static Statistics fromJSON(String contents){
        JSONObject json = new JSONObject(contents);
        float[] parsed_properties = new int[FarmConfig.PROPERTIES_NUMBER];
        for(int i = 0; i < FarmConfig.PROPERTIES_NUMBER; i ++ ){
            parsed_properties[i] = json.getInt(Statistics.STATISTICS_NAMES[i]);
    }
*/
    public static Statistics fromBytes(byte[] bytes){
        if(isNull(bytes)) return null;
        Statistics statistics = new Statistics();
        if (bytes.length <= 4){return null;}
        ByteBuffer b = ByteBuffer.wrap(bytes);
        statistics.length = b.getInt();

        statistics.time = new long[statistics.length];
        statistics.data = new double[STATISTICS_COUNT][statistics.length];

        /*statistics.waterLevel = new float[statistics.length];
        statistics.temperature = new float[statistics.length];
        statistics.aridity = new float[statistics.length];*/

        for(int i = 0; i < statistics.length; i ++ ){
            statistics.time[i] = b.getLong();
            for(int j = 0; j < STATISTICS_COUNT; j ++){
                statistics.data[j][i] = b.getDouble();
            }
        }
        return statistics;
    }

    public static Statistics fromStream(DataInputStream dIn){
        Statistics statistics = new Statistics();
        try {
            statistics.length = dIn.readInt();
            statistics.time = new long[statistics.length];
            statistics.data = new double[STATISTICS_COUNT][statistics.length];
            for (int i = 0; i < statistics.length; i++) {
                statistics.time[i] = dIn.readLong();
                for (int j = 0; j < STATISTICS_COUNT; j++) {
                    statistics.data[j][i] = dIn.readDouble();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return statistics;
    }

    public byte[] toBytes(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + this.length*(8 + 3*4));
        byteBuffer.putInt(this.length);
        for(int i = 0; i < this.length; i ++ ){
            byteBuffer.putLong(this.time[i]);
            for(int j = 0; j < STATISTICS_COUNT; j ++){
                byteBuffer.putDouble(this.data[i][j]);
            }
        }
        return byteBuffer.array();
    }

    public double[] getData(int i){
        return data[i];
    }

    public long[] getTime(){
        return time;
    }

    public Statistics(){
    }
}
