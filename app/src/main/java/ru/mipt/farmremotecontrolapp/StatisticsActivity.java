package ru.mipt.farmremotecontrolapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mipt.ru.mipt.farmremotecontrolapp.R;

public class StatisticsActivity extends AppCompatActivity {

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
//        ImageView imageView = findViewById(R.id.graphic);
//        Bitmap bm = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), null);
//        Canvas canvas = new Canvas();
//        canvas.setBitmap(bm);
//        imageView.setImageBitmap();
//        canvas.
    }
}