package com.example.silencer.NightPreference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.silencer.R;
import com.example.silencer.Receiver.AlarmReceiver;

import java.util.Calendar;

public class NightPreferenceManager {
    private Context mContext;

    public NightPreferenceManager(Context context) {
        mContext = context;
    }

    public void setAlarm(String action) {
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action, null, mContext, AlarmReceiver.class);
        cancelAlarmIfAlreadyExists(manager, intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        int time = getTimeFromAction(action);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time / 60);
        calendar.set(Calendar.MINUTE, time % 60);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm(String action) {
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action, null, mContext, AlarmReceiver.class);
        cancelAlarmIfAlreadyExists(manager, intent);
    }

    private void cancelAlarmIfAlreadyExists(AlarmManager alarmManager, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            Log.d("lkjhj", "cancelling alarm");
            alarmManager.cancel(pendingIntent);
        }
    }

    private int getTimeFromAction(String action) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        mContext.getResources().getString(R.string.app_preferences),
                        Context.MODE_PRIVATE);

        String preferenceKey;
        int defaultTime;

        if (action.equals(mContext.getResources().getString(R.string.start_time_alarm))) {
            preferenceKey = mContext.getResources().getString(R.string.start_time_key);
            defaultTime = mContext.getResources().getInteger(R.integer.default_start_time);
        } else {
            preferenceKey = mContext.getResources().getString(R.string.end_time_key);
            defaultTime = mContext.getResources().getInteger(R.integer.default_end_time);
        }

        return sharedPreferences.getInt(preferenceKey, defaultTime);
    }

}
