package com.jubaldo.fthangouts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jubaldo.fthangouts.adapter.ContactAdapter;
import com.jubaldo.fthangouts.db.DBHelper;
import com.jubaldo.fthangouts.model.Contact;

import java.util.List;

/**
 * Activity: represents a screen in the app. AppCompatActivity adds the compatibility with older
 * Android versions, which will allow us to handle the Toolbar (header's color menu)
 */
public class MainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static final int PERMISSION_REQUEST_SMS = 100;

    /**
     * Activity Lifecycle (subject PDF): onCreate() is the first call ever at the screen creation.
     * It contains all the initialization code.
     * Rule: always call super.onCreate() first, or else it will crash.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Links this Java code to file res/layout/activity_main.xml:
        // XML file that visually defines the screen (buttons, lists, etc...).
        // R: auto-generated class by Android Studio that references all our resources
        // (layout, strings, images...). We never edit it ourselves.
        setContentView(R.layout.activity_main);
        setupToolbar();

        // Complements EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestSmsPermissionsIfNeeded();

        recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Intent: message sent to Android system to ask to do something:
        // here, we ask "launch the AddEditContactActivity activity".
        // startActivity(): run this Intent, that displays the new screen on top of the current screen.
        findViewById(R.id.fabAddContact).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditContactActivity.class)));
    }

    /**
     * onResume() is called every time the activity comes back to the foreground.
     * We reload the contacts list here so new or modified contacts appear immediately
     * when returning from AddEditContactActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        List<Contact> contacts;
        try (DBHelper dbHelper = new DBHelper(this)) {
            contacts = dbHelper.getAllContacts();
        }

        recyclerView.setAdapter(new ContactAdapter(contacts, contact -> {
            Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.putExtra(ContactDetailActivity.EXTRA_CONTACT_ID, contact.getId());
            startActivity(intent);
        }));
    }

    /**
     * Checks if all required SMS permissions (SEND_SMS, READ_SMS, RECEIVE_SMS) are granted.
     * Prompts the system permission dialog if any of them is missing.
     */
    private void requestSmsPermissionsIfNeeded() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_SMS);
        }
    }
}
