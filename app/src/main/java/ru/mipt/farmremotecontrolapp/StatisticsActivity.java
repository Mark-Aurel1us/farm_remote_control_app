package ru.mipt.farmremotecontrolapp;

import static ru.mipt.farmremotecontrolapp.Utils.plotGraph;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mipt.ru.mipt.farmremotecontrolapp.R;

public class StatisticsActivity extends AppCompatActivity {
    final static String TAG = "StatisticsActivity";
    ImageView imageView;
    Statistics statistics = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.imageView = new ImageView(StatisticsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000,1000);
        imageView.setLayoutParams(lp);
        LinearLayout linearLayout = findViewById(R.id.statistics_linear_layout);
        linearLayout.addView(imageView);

//        Intent intent = getIntent();
//        byte[] statsBytes = intent.getByteArrayExtra("stats");
//        statistics = Statistics.fromBytes(statsBytes);
        Button temperature_button = findViewById(R.id.temperature_button);
        Button waterlevel_button = findViewById(R.id.waterlevel_button);
        temperature_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawGraphic(new long[]{10000, 20000, 30000, 40000, 50000}, new float[]{1, 4, 3, 7, 6}, "Temperature");
            }
        });
        waterlevel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawGraphic(new long[]{10000, 20000, 30000}, new float[]{1, 3, 2}, "Water level");
            }
        });

    }
    void drawGraphic(long[] time, float[] data, String name){
        try {
            Bitmap bitmap = plotGraph(time, data, "Time", name, 1000, 1000);
            imageView.setImageBitmap(bitmap);
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            imageView.setImageBitmap(bitmap);
//            imageView.post(new Runnable() {
//                @Override
//                public void run() {
//                    imageView.setImageBitmap(bitmap);
//                }
//            });
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            findViewById(R.id.statistics_linear_layout).setBackground(drawable);

            imageView.setVisibility(View.VISIBLE);
            //Log.d(TAG,imageView.toString());
            //Log.d(TAG,bitmap.toString());
            //Log.d(TAG,""+linearLayout.getChildCount());
        } catch (Exception e){
            Log.d(TAG,"Oh no!");
            Log.d(TAG,e.getMessage());
        }
    }
}