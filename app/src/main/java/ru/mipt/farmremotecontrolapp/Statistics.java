package ru.mipt.farmremotecontrolapp;

import java.nio.ByteBuffer;

public class Statistics {
    private long[] time;
    private float[] waterLevel;
    private float[] temperature;
    private float[] aridity;
    private int length;

    public static Statistics fromBytes(byte[] bytes){
        Statistics statistics = new Statistics();
        if (bytes.length <= 4){return null;}
        ByteBuffer b = ByteBuffer.wrap(bytes);
        statistics.length = b.getInt();

        statistics.waterLevel = new float[statistics.length];
        statistics.temperature = new float[statistics.length];
        statistics.aridity = new float[statistics.length];

        for(int i = 0; i < statistics.length; i ++ ){
            statistics.time[i] = b.getInt();
            statistics.waterLevel[i] = b.getFloat();
            statistics.temperature[i] = b.getFloat();
            statistics.aridity[i] = b.getFloat();
        }

        return statistics;
    }

    public byte[] toBytes(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + this.length*(8 + 3*4));
        byteBuffer.putInt(this.length);
        for(int i = 0; i < this.length; i ++ ){
            byteBuffer.putLong(this.time[i]);
            byteBuffer.putFloat(this.waterLevel[i]);
            byteBuffer.putFloat(this.temperature[i]);
            byteBuffer.putFloat(this.aridity[i]);
        }
        return byteBuffer.array();
    }

    public Statistics(){
    }
}
