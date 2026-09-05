package com.jubaldo.fthangouts;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Activity: represents a screen in the app. AppCompatActivity adds the compatibility with older
// Android versions, which will allow us to handle the Toolbar (header's color menu)
public class MainActivity extends AppCompatActivity {

    @Override
    /*
        Activity Lifecycle (subject PDF): onCreate() is the first call ever at the screen creation.
        It contains all the initialization code.
        Rule: always call super.onCreate() first, or else it will crash.
    */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Links this Java code to file res/layout/activity_main.xml:
        // XML file that visually defines the screen (buttons, lists, etc...).
        // R: auto-generated class by Android Studio that references all our ressources
        // (layout, strings, images...). We never edit it ourselves.
        setContentView(R.layout.activity_main);

        // Complements EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}