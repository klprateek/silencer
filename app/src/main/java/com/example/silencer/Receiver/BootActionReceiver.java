package com.example.silencer.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.silencer.R;
import com.example.silencer.Service.SilencerService;

public class BootActionReceiver extends BroadcastReceiver {
    private static String TAG = "BootActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Received some intent", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        context.getResources().getString(R.string.app_preferences),
                        Context.MODE_PRIVATE);

//        String serviceKey = context.getResources().getString(R.string.service_preference_key);
//        boolean serviceStatus = sharedPreferences.getBoolean(serviceKey, false);
//        if (!serviceStatus) return;

        Intent serviceIntent = new Intent(context, SilencerService.class);
        context.startForegroundService(serviceIntent);
    }
}