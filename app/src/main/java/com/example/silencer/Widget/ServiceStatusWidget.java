package com.example.silencer.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.silencer.NightPreference.NightPreferenceManager;
import com.example.silencer.R;

public class ServiceStatusWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, android.appwidget.AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
        Intent intent = new Intent(context, ServiceStatusWidget.class);
        intent.setAction("action_widget");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widgetTextView, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if ("action_widget".equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.app_preferences), Context.MODE_PRIVATE);
            boolean serviceStatus = prefs.getBoolean(context.getResources().getString(R.string.service_preference_key), false);
            Log.d("lkjhj", "onReceive status = " + serviceStatus);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(context.getResources().getString(R.string.service_preference_key), !serviceStatus);
            editor.apply();
            Log.d("lkjhj", "onReceive status changed to = " + !serviceStatus);

        }
    }
}
