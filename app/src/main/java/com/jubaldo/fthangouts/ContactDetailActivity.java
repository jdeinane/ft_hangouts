package com.jubaldo.fthangouts;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jubaldo.fthangouts.db.DBHelper;
import com.jubaldo.fthangouts.model.Contact;

public class ContactDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_ID = "contact_id";

    private int contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        contactId = getIntent().getIntExtra(EXTRA_CONTACT_ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Contact contact;
        try (DBHelper dbHelper = new DBHelper(this)) {
            contact = dbHelper.getContactById(contactId);
        }

        if (contact == null) {
            finish();
            return;
        }

        TextView textDetailName = findViewById(R.id.textDetailName);
        TextView textDetailPhone = findViewById(R.id.textDetailPhone);
        TextView textDetailEmail = findViewById(R.id.textDetailEmail);
        TextView textDetailBirthday = findViewById(R.id.textDetailBirthday);

        textDetailName.setText(getString(R.string.format_full_name, contact.getFirstName(), contact.getLastName()));
        textDetailPhone.setText(contact.getPhoneNumber());
        textDetailEmail.setText(contact.getEmail());
        textDetailBirthday.setText(contact.getBirthday());
    }
}
