package ru.mipt.farmremotecontrolapp;

import static java.util.Objects.isNull;

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
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import ru.mipt.ru.mipt.farmremotecontrolapp.R;

//import  com.oracle.common.base.Timeout;

public class StatisticsActivity extends AppCompatActivity {
    final static String TAG = "StatisticsActivity";
    //ImageView imageView;
    Statistics statistics = null;
    GraphicView graphicView;
    TableLayout table;
    ScheduledExecutorService scheduler;

    boolean showTable = false;

    int lastGraph = 0;

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
                    StatisticsActivity.this.statistics = controlMessage.getStats();
                    drawGraphic(statistics.getTime(), statistics.getData(lastGraph), Statistics.STATISTICS_NAMES[lastGraph]);
                }
            });

            scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> handle = scheduler.scheduleWithFixedDelay(controlMessage, 0, 10, java.util.concurrent.TimeUnit.SECONDS);


        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        //

        StatisticsActivity.this.graphicView = new GraphicView(StatisticsActivity.this);
        LinearLayout linearLayout = findViewById(R.id.statistics_linear_layout);

        for(int i = 0; i < Statistics.STATISTICS_COUNT; i++){
            Button button = new Button(StatisticsActivity.this);
            button.setText(Statistics.STATISTICS_NAMES_RU[i]);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isNull(statistics)) drawGraphic(statistics.getTime(), statistics.getData(finalI), Statistics.STATISTICS_NAMES[finalI]);
                    StatisticsActivity.this.lastGraph = finalI;
                }
            });
            linearLayout.addView(button);
        }

        Button button = new Button(StatisticsActivity.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button)v).setText(StatisticsActivity.this.showTable ? getText(R.string.show_full_table) : getText(R.string.hide_full_table));
                drawGraphic(statistics.getTime(), statistics.getData(lastGraph), Statistics.STATISTICS_NAMES[lastGraph]);
                StatisticsActivity.this.showTable = !StatisticsActivity.this.showTable;

            }
        });
        button.setText(R.string.show_full_table);

        linearLayout.addView(graphicView);

        linearLayout.addView(button);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        linearLayout.addView(scrollView);
        table = new TableLayout(this);
        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        scrollView.addView(table);


    }

    void drawGraphic(long[] time, double[] data, String name){
        try {
            StatisticsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    graphicView.setGraphicParams(time, data, "Time", name);
                    graphicView.callOnClick();

                    table.removeAllViews();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm:ss", Locale.getDefault());
                    int len = time.length;
                    if(!showTable) {len = 100;} //else {len = 100;}
                    for (int i = 0; i < time.length && i < len; i+= 10) {
                        TableRow tableRow = new TableRow(StatisticsActivity.this);
                        LinearLayout horisontalLinearLayout = new LinearLayout(StatisticsActivity.this);
                        horisontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        //horisontalLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        TextView timeText = new TextView(StatisticsActivity.this);
                        timeText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                        timeText.setText(sdf.format(new Date(time[i] * 1000)));
                        horisontalLinearLayout.addView(timeText);

                        TextView dataText = new TextView(StatisticsActivity.this);
                        dataText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                        dataText.setText(String.valueOf(data[i]));
                        horisontalLinearLayout.addView(dataText);

                        tableRow.addView(horisontalLinearLayout);
                        table.addView(tableRow);
                    }
                }
            });



        } catch (Exception e){
            Log.d(TAG,"Oh no!");
            Log.d(TAG,e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        scheduler.shutdown();
        super.onDestroy();
    }
}