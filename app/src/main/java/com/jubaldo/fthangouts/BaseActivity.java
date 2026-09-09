package com.jubaldo.fthangouts;

import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Base class for every screen of the app.
 * Wires up the Toolbar (used as the app's header) and the "change header color" menu,
 * and applies the color the user last picked so it stays consistent across all activities.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ft_hangouts_prefs";
    private static final String KEY_HEADER_COLOR = "header_color";

    /**
     * Must be called by subclasses in onCreate(), after setContentView(),
     * once the layout's Toolbar (R.id.toolbar) is available.
     */
    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        applyHeaderColor();
    }

    private void applyHeaderColor() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        toolbar.setBackgroundColor(getSavedHeaderColor());
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
