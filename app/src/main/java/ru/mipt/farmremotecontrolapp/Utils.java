package ru.mipt.farmremotecontrolapp;

import static java.util.Objects.isNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
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
}
