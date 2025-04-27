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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;

import ru.mipt.ru.mipt.farmremotecontrolapp.R;

public class StatisticsActivity extends AppCompatActivity {
    final static String TAG = "StatisticsActivity";
    //ImageView imageView;
    Statistics statistics = null;
    GraphicView graphicView;

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



        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("json");
        try {
            JSONObject json = new JSONObject(jsonString);
            ControlMessage controlMessage = ControlMessage.getStatisticsMessage(json);
            controlMessage.setOnReceiveListener(new ControlMessage.OnReceiveListener() {
                @Override
                void receive(ControlMessage controlMessage) {
                    statistics = controlMessage.getStats();

                    StatisticsActivity.this.graphicView = new GraphicView(StatisticsActivity.this);
                    LinearLayout linearLayout = findViewById(R.id.statistics_linear_layout);

                    for(int i = 0; i < FarmCommand.COMMAND_COUNT; i++){
                        Button button = new Button(StatisticsActivity.this);
                        button.setText(FarmCommand.COMMAND_NAMES_RU[i]);
                        int finalI = i;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawGraphic(statistics.getTime(), statistics.getData(finalI), Statistics.STATISTICS_NAMES[finalI]);
                            }
                        });
                        linearLayout.addView(button);
                    }

                    linearLayout.addView(graphicView);



                }
            });
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }




    }
    void drawGraphic(long[] time, double[] data, String name){
        try {
            graphicView.setGraphicParams(time, data, "Time", name);
            graphicView.callOnClick();
        } catch (Exception e){
            Log.d(TAG,"Oh no!");
            Log.d(TAG,e.getMessage());
        }
    }
}