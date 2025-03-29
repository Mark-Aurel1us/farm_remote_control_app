package ru.mipt.farmremotecontrolapp;

import static java.util.Objects.isNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import android.graphics.Color;
import android.graphics.Paint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    static final String TAG = "Utils";
    @SuppressLint("NewApi")
    public static void setViewWeight(View view, int weight){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, weight);
        view.setLayoutParams(param);
    }

    public static Path pathFromIntent(Intent intent){
        if(isNull(intent)){return null;}
        try {
            Uri config_uri = intent.getData();
            @SuppressLint({"NewApi", "LocalSuppress"})
            Path config_path = Paths.get(config_uri.getPath());
            return config_path;
        } catch (Exception ignored){}
        return null;
    }

    public static Bitmap plotGraph(long[] timestamps, float[] values,
                                   String xLabel, String yLabel,
                                   int width, int height) {
        if (timestamps == null || values == null || timestamps.length != values.length || timestamps.length == 0){
            throw new IllegalArgumentException("Invalid input data");
        }
        Log.d(TAG,"Width:"+width+";Height:"+height+";");
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, conf);


        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paintAxis = new Paint();
        paintAxis.setColor(Color.BLACK);
        paintAxis.setStrokeWidth(5);

        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(30);

        Paint paintLine = new Paint();
        paintLine.setColor(Color.BLUE);
        paintLine.setStrokeWidth(3);

        int padding = 80;
        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;

        // Определение минимального и максимального значений
        float minValue = Float.MAX_VALUE, maxValue = Float.MIN_VALUE;
        for (float v : values){
            if (v < minValue) minValue = v;
            if (v > maxValue) maxValue = v;
        }

        // Рисуем оси
        int xAxisY = height - padding;
        int yAxisX = padding;
        canvas.drawLine(yAxisX, padding, yAxisX, xAxisY, paintAxis);
        canvas.drawLine(yAxisX, xAxisY, width - padding, xAxisY, paintAxis);

        // Подписи осей
        canvas.drawText(xLabel, width / 2f, height - 20, paintText);
        canvas.drawText(yLabel, 20, height / 2f, paintText);

        // Отображение значений по X (время)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        for (int i = 0; i < timestamps.length; i++) {
            float x = yAxisX + (timestamps[i] - timestamps[0]) * graphWidth / (timestamps[timestamps.length - 1] - timestamps[0]);
            String timeLabel = sdf.format(new Date(timestamps[i] * 1000));
            canvas.drawText(timeLabel, x, xAxisY + 40, paintText);
        }

        // Отображение значений по Y
        for (int i = 0; i < 5; i++) {
            float value = minValue + i * (maxValue - minValue) / 4;
            float y = xAxisY - (value - minValue) * graphHeight / (maxValue - minValue);
            canvas.drawText(String.format(Locale.US, "%.1f", value), 5, y, paintText);
        }

        // Рисуем линии графика
        for (int i = 0; i < timestamps.length - 1; i++) {
            float x1 = yAxisX + (timestamps[i] - timestamps[0]) * graphWidth / (timestamps[timestamps.length - 1] - timestamps[0]);
            float y1 = xAxisY - (values[i] - minValue) * graphHeight / (maxValue - minValue);
            float x2 = yAxisX + (timestamps[i + 1] - timestamps[0]) * graphWidth / (timestamps[timestamps.length - 1] - timestamps[0]);
            float y2 = xAxisY - (values[i + 1] - minValue) * graphHeight / (maxValue - minValue);
            canvas.drawLine(x1, y1, x2, y2, paintLine);
        }

        return bitmap;
    }

}
