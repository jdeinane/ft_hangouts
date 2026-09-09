package com.jubaldo.fthangouts;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    // A screen rotation destroys and recreates the current activity (onStop then onStart),
    // which briefly drops startedActivityCount to 0 exactly like a real backgrounding would.
    // Debouncing the "backgrounded" transition lets a same-app stop/start pair (rotation,
    // or a new activity of this app opening) cancel itself out before it is treated as real.
    private static final long BACKGROUND_DEBOUNCE_MS = 500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable saveBackgroundTimestampRunnable = this::saveBackgroundTimestamp;

    private int startedActivityCount = 0;
    private boolean backgroundSavePending = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        startedActivityCount++;

        if (startedActivityCount == 1) {
            handler.removeCallbacks(saveBackgroundTimestampRunnable);

            if (!backgroundSavePending) {
                showLastBackgroundedToast(activity);
            }
            backgroundSavePending = false;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        startedActivityCount--;

        if (startedActivityCount == 0) {
            backgroundSavePending = true;
            handler.postDelayed(saveBackgroundTimestampRunnable, BACKGROUND_DEBOUNCE_MS);
        }
    }

    private void saveBackgroundTimestamp() {
        backgroundSavePending = false;

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
