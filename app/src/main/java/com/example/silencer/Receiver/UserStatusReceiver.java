package com.example.silencer.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import com.example.silencer.R;

import java.util.Calendar;

public class UserStatusReceiver extends BroadcastReceiver {
    private static String TAG = "UserStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        context.getResources().getString(R.string.app_preferences),
                        Context.MODE_PRIVATE);

        String startTimeKey = context.getResources().getString(R.string.start_time_key);
        int st = sharedPreferences.getInt(startTimeKey, 601);
        String endTimeKey = context.getResources().getString(R.string.end_time_key);
        int et = sharedPreferences.getInt(endTimeKey, 301);
        String nightKey = context.getResources().getString(R.string.night_preference_key);
        boolean nt = sharedPreferences.getBoolean(nightKey, false);

        Calendar calendar = Calendar.getInstance();
        int ct = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        if (nt && currentTimeWithinRange(st, et, ct)) {
            return;
        }

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (intent.getAction() == Intent.ACTION_USER_PRESENT) {
            Log.i(TAG, "onReceive ACTION_USER_PRESENT");
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                Log.d(TAG, "Ringer is set to silent");
                return;
            }
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                Log.d(TAG, "Ringer is set to silent");
                return;
            }
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.i(TAG, "onReceive ACTION_SCREEN_OFF");
        }
    }

    private boolean currentTimeWithinRange(int st, int et, int ct) {
        if (st < et) return ct >= st && ct <= et;
        else return ct >= st || ct <= et;
    }
}