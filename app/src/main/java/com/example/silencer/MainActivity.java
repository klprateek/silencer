package com.example.silencer;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.silencer.NightPreference.NightPreferenceManager;
import com.example.silencer.Receiver.BootActionReceiver;
import com.example.silencer.Service.SilencerService;
import com.example.silencer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private MainActivityViewModel viewModel;
    private ActivityMainBinding binding;
    private NightPreferenceManager mNightPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNightPreferenceManager = new NightPreferenceManager(MainActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Get the reference for the view model.
        viewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(MainActivityViewModel.class);

        // Set view model reference to binding so that xml can directly access mutable live data
        // of the view model.
        binding.setMainActivityViewModel(viewModel);
        binding.setLifecycleOwner(this);


        binding.startTimeLayout.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        viewModel.onStartTimeChanged(hourOfDay, minute);
                        mNightPreferenceManager.setAlarm(
                                getResources().getString(R.string.start_time_alarm));
                    },
                    viewModel.startTime.getValue() / 60,
                    viewModel.startTime.getValue() % 60,
                    false);
            timePicker.show();
        });

        binding.endTimeLayout.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        viewModel.onEndTimeChanged(hourOfDay, minute);
                        mNightPreferenceManager.setAlarm(
                                getResources().getString(R.string.end_time_alarm));
                    },
                    viewModel.endTime.getValue() / 60,
                    viewModel.endTime.getValue() % 60,
                    false);
            timePicker.show();
        });

        Intent serviceIntent = new Intent(this, SilencerService.class);
        ComponentName componentName = new ComponentName(MainActivity.this, BootActionReceiver.class);
        viewModel.serviceStatus.observe(this, aBoolean -> {
            if (aBoolean) {
                startAppService(serviceIntent, componentName);
            } else {
                stopAppService(serviceIntent, componentName);
            }
        });

        viewModel.nightStatus.observe(this, aBoolean -> {
            setNightPreference(aBoolean);
        });

    }

    private void startAppService(Intent serviceIntent, ComponentName componentName) {
        startForegroundService(serviceIntent);
        getPackageManager().setComponentEnabledSetting(
                componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        setNightPreference(viewModel.nightStatus.getValue());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    private void stopAppService(Intent serviceIntent, ComponentName componentName) {
        stopService(serviceIntent);
        getPackageManager().setComponentEnabledSetting(
                componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        setNightPreference(false);
    }

    private void setNightPreference(Boolean enabled) {
        String startTimeAction = getResources().getString(R.string.start_time_alarm);
        String endTimeAction = getResources().getString(R.string.end_time_alarm);
        if (enabled) {
            mNightPreferenceManager.setAlarm(startTimeAction);
            mNightPreferenceManager.setAlarm(endTimeAction);
        } else {
            mNightPreferenceManager.cancelAlarm(startTimeAction);
            mNightPreferenceManager.cancelAlarm(endTimeAction);
        }
    }
}
