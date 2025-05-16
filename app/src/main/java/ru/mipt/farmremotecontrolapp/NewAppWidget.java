package ru.mipt.farmremotecontrolapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import ru.mipt.ru.mipt.farmremotecontrolapp.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewAppWidgetConfigureActivity NewAppWidgetConfigureActivity}
 */
public class NewAppWidget extends AppWidgetProvider {

    ScheduledExecutorService scheduledExecutorService;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d(NewAppWidget.class.toString(), "widget update started");
        ControlMessage controlMessage = ControlMessage.getLastStatisticMessage();
        controlMessage.setOnReceiveListener(new ControlMessage.OnReceiveListener() {
            @Override
            void receive(ControlMessage controlMessage) {
                Statistics statistics = controlMessage.getStats();
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                String summary = "";
                for(int i = 0; i < Statistics.STATISTICS_COUNT; i++){
                    summary += Statistics.STATISTICS_NAMES[i] + ":" + statistics.getData(i)[0] + "\n";
                }
                views.setTextViewText(R.id.appwidget_text, summary);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
                Log.d(NewAppWidget.class.toString(), "widget updated");
            }
        });
        Thread thread = new Thread(controlMessage);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> handle = scheduledExecutorService.scheduleWithFixedDelay(controlMessage, 0, 10, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            //NewAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        scheduledExecutorService.shutdown();
        super.onDisabled(context);
        // Enter relevant functionality for when the last widget is disabled
    }
}