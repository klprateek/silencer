package com.example.silencer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class MainActivityViewModel extends AndroidViewModel {
    private static String TAG = "MainActivityViewModel";
    private Application mApplication;
    private SharedPreferences sharedPreferences;
    private String servicePreferenceKey, nightPreferenceKey, startTimeKey, endTimeKey;
    public MutableLiveData<Boolean> serviceStatus, nightStatus, nightEnabled, timeLayoutEnabled;
    public MutableLiveData<Integer> startTime, endTime, timeLayoutVisible;
    public LiveData<String> startTimeText;
    public LiveData<String> endTimeText;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        sharedPreferences = application.getSharedPreferences(
                application.getResources().getString(R.string.app_preferences),
                Context.MODE_PRIVATE);
        initSharedPreferenceValues();

    }

    private void initSharedPreferenceValues() {
        servicePreferenceKey = getApplication().getResources().getString(R.string.service_preference_key);
        serviceStatus = new MutableLiveData<>();
        serviceStatus.setValue(sharedPreferences.getBoolean(servicePreferenceKey, true));

        nightPreferenceKey = getApplication().getResources().getString(R.string.night_preference_key);
        nightStatus = new MutableLiveData<>();
        nightStatus.setValue(sharedPreferences.getBoolean(nightPreferenceKey, false));
        nightEnabled = new MutableLiveData<>(serviceStatus.getValue());


        startTimeKey = getApplication().getResources().getString(R.string.start_time_key);
        startTime = new MutableLiveData<>();
        startTime.setValue(sharedPreferences.getInt(startTimeKey, mApplication.getResources().getInteger(R.integer.default_start_time)));
        startTimeText = Transformations.map(startTime, input -> convertTimeToText(input));

        endTimeKey = getApplication().getResources().getString(R.string.end_time_key);
        endTime = new MutableLiveData<>();
        endTime.setValue(sharedPreferences.getInt(endTimeKey, mApplication.getResources().getInteger(R.integer.default_end_time)));
        endTimeText = Transformations.map(endTime, input -> convertTimeToText(input));

        timeLayoutEnabled = new MutableLiveData<>(nightEnabled.getValue());
        timeLayoutVisible = new MutableLiveData<>();
        timeLayoutVisible.setValue(nightStatus.getValue() ? View.VISIBLE : View.GONE);

        adjustServicePreferenceVisibility();
    }

    private void adjustServicePreferenceVisibility() {
        adjustNightPreferenceVisibility();
    }

    private void adjustNightPreferenceVisibility() {
        nightEnabled.setValue(serviceStatus.getValue());
        adjustTimeLayoutVisibility();
    }

    private void adjustTimeLayoutVisibility() {
        timeLayoutEnabled.setValue(nightEnabled.getValue());
        timeLayoutVisible.setValue(nightStatus.getValue() ? View.VISIBLE : View.GONE);
    }

    public void serviceStatusCheckedChange(boolean checked) {
        if (serviceStatus.getValue() == checked) return;
        serviceStatus.setValue(checked);
        insertInSharedPrefs(sharedPreferences, servicePreferenceKey, checked);
        adjustServicePreferenceVisibility();
    }

    public void nightStatusCheckedChange(boolean checked) {
        if (nightStatus.getValue() == checked) return;
        nightStatus.setValue(checked);
        insertInSharedPrefs(sharedPreferences, nightPreferenceKey, checked);
        adjustNightPreferenceVisibility();
    }

    public void onStartTimeChanged(int hourOfDay, int minute) {
        startTime.setValue(hourOfDay * 60 + minute);
        insertInSharedPrefs(sharedPreferences, startTimeKey, startTime.getValue());
    }

    public void onEndTimeChanged(int hourOfDay, int minute) {
        endTime.setValue(hourOfDay * 60 + minute);
        insertInSharedPrefs(sharedPreferences, endTimeKey, endTime.getValue());
    }

    private void insertInSharedPrefs(SharedPreferences sharedPreferences, String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void insertInSharedPrefs(SharedPreferences sharedPreferences, String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    private String convertTimeToText(int time) {
        int hourOfDay = time / 60;
        int minute = time % 60;
        int hour = (hourOfDay < 12 ? hourOfDay : hourOfDay - 12);
        String hourText = "" + (hour < 10 ? "0" + hour : hour);
        String minuteText = "" + (minute < 10 ? "0" + minute : minute);
        return hourText + ":" + minuteText + (hourOfDay < 12 ? " AM" : " PM");
    }
}
