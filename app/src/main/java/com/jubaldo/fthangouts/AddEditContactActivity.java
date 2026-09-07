package com.jubaldo.fthangouts;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jubaldo.fthangouts.db.DBHelper;
import com.jubaldo.fthangouts.model.Contact;

/*
    - Load the visual XML file (activity_add_edit_contact.xml) via setContentView().
    - Link the screen's visual elements to Java code by their ids (R.id.editFirstName, etc.).
    - Validate the entered data.
    - Save in database and close the screen.
*/
public class AddEditContactActivity extends AppCompatActivity {

    // EditText allows user to write and modify text on screen (equivalent of <input type="text">).
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editPhoneNumber;
    private EditText editEmail;
    private EditText editBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editEmail = findViewById(R.id.editEmail);
        editBirthday = findViewById(R.id.editBirthday);

        // Attach the "Click Listener" to the button.
        // v -> saveContact() is a lambda function: for each click, Android calls this function
        // that calls saveContact().
        findViewById(R.id.buttonSave).setOnClickListener(v -> saveContact());
    }

    private void saveContact() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String birthday = editBirthday.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "First name, last name and phone number are required!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Contact contact = new Contact(firstName, lastName, phoneNumber, email, birthday);

        try (DBHelper dbHelper = new DBHelper(this)) {
            dbHelper.insertContact(contact);
        }

        // Close this activity and automatically go back to previous screen (MainActivity)
        // (equivalent to "Return" button).
        finish();
    }
}
