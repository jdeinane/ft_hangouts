package com.jubaldo.fthangouts;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Custom Application class that monitors app-wide lifecycle events.
 * Tracks when the app enters background or foreground using ActivityLifecycleCallbacks.
 * Saves the timestamp in SharedPreferences when backgrounded, and displays a Toast
 * with the last backgrounded date and time when brought back to the foreground.
 */
public class FtHangoutsApplication extends Application implements
Application.ActivityLifecycleCallbacks {

    private static final String PREFS_NAME = "ft_hangouts_prefs";
    private static final String KEY_LAST_BACKGROUND_TIMESTAMP = "last_background_timestamp";

    private int startedActivityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        startedActivityCount++;

        if (startedActivityCount == 1) {
            showLastBackgroundedToast(activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        startedActivityCount--;

        if (startedActivityCount == 0) {
            saveBackgroundTimestamp();
        }
    }

    private void saveBackgroundTimestamp() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putLong(KEY_LAST_BACKGROUND_TIMESTAMP, System.currentTimeMillis()).apply();
    }

    private void showLastBackgroundedToast(Activity activity) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long timestamp = prefs.getLong(KEY_LAST_BACKGROUND_TIMESTAMP, -1);

        if (timestamp == -1) {
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(timestamp));

        Toast.makeText(activity, activity.getString(R.string.format_last_backgrounded, formattedDate),
                Toast.LENGTH_LONG).show();
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityResumed(Activity activity) {}
    @Override public void onActivityPaused(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
