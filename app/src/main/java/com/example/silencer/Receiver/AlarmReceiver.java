package com.example.silencer.Receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.example.silencer.NightPreference.NightPreferenceManager;
import com.example.silencer.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if(!keyguardManager.inKeyguardRestrictedInputMode()) return;

        String startTimeAction = context.getResources().getString(R.string.start_time_alarm);
        String endTimeAction = context.getResources().getString(R.string.end_time_alarm);
        NightPreferenceManager nightPreferenceManager = new NightPreferenceManager(context);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (startTimeAction.equals(intent.getAction())) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            nightPreferenceManager.setAlarm(startTimeAction);

        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            nightPreferenceManager.setAlarm(endTimeAction);
        }
    }
}