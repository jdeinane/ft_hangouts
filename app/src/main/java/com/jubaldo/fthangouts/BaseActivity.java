package com.jubaldo.fthangouts;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for every screen of the app.
 * Wires up the Toolbar (used as the app's header) and the "change header color" menu,
 * and applies the color the user last picked so it stays consistent across all activities.
 * Screens can also register their buttons/FAB via registerColorableViews() to have them
 * tinted with the same color as the header.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ft_hangouts_prefs";
    private static final String KEY_HEADER_COLOR = "header_color";

    private final List<View> colorableViews = new ArrayList<>();

    /**
     * Must be called by subclasses in onCreate(), after setContentView(),
     * once the layout's Toolbar (R.id.toolbar) is available.
     */
    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        applyHeaderColor();
    }

    /**
     * Registers buttons/FAB that should be tinted with the same color as the header.
     * Call once per screen, after the views are inflated (typically right after setupToolbar()).
     */
    protected void registerColorableViews(View... views) {
        colorableViews.addAll(Arrays.asList(views));
        applyHeaderColor();
    }

    /**
     * Re-applies the saved color every time the screen comes back to the foreground.
     * Needed because navigating Back resumes an existing activity instance instead of
     * recreating it, so onCreate()/setupToolbar() would otherwise never re-run and the
     * screen could keep showing whatever color was current when it was first created.
     */
    @Override
    protected void onResume() {
        super.onResume();
        applyHeaderColor();
    }

    private void applyHeaderColor() {
        int color = getSavedHeaderColor();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }

        ColorStateList tint = ColorStateList.valueOf(color);
        for (View view : colorableViews) {
            view.setBackgroundTintList(tint);
        }
    }

    private int getSavedHeaderColor() {
        int defaultColor = getColor(R.color.header_color_default);
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(KEY_HEADER_COLOR, defaultColor);
    }

    private void saveHeaderColor(int color) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putInt(KEY_HEADER_COLOR, color).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_header_color, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int colorRes = colorResForMenuItem(item.getItemId());

        if (colorRes != 0) {
            saveHeaderColor(getColor(colorRes));
            applyHeaderColor();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int colorResForMenuItem(int itemId) {
        if (itemId == R.id.menu_color_default) {
            return R.color.header_color_default;
        } else if (itemId == R.id.menu_color_red) {
            return R.color.header_color_red;
        } else if (itemId == R.id.menu_color_green) {
            return R.color.header_color_green;
        } else if (itemId == R.id.menu_color_blue) {
            return R.color.header_color_blue;
        } else if (itemId == R.id.menu_color_pink) {
            return R.color.header_color_pink;
        }
        return 0;
    }
}
