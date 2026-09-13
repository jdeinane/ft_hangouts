package com.jubaldo.fthangouts;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jubaldo.fthangouts.db.DBHelper;
import com.jubaldo.fthangouts.model.Contact;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * - Load the visual XML file (activity_add_edit_contact.xml) via setContentView().
 * - Link the screen's visual elements to Java code by their ids (R.id.editFirstName, etc.).
 * - Validate the entered data.
 * - Save in database and close the screen.
 */
public class AddEditContactActivity extends BaseActivity {

    public static final String EXTRA_CONTACT_ID = "contact_id";

    // EditText allows user to write and modify text on screen (equivalent of <input type="text">).
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editPhoneNumber;
    private EditText editEmail;
    private EditText editBirthday;

    private int contactId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);
        setupToolbar();

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editEmail = findViewById(R.id.editEmail);
        editBirthday = findViewById(R.id.editBirthday);

        Button buttonSave = findViewById(R.id.buttonSave);

        contactId = getIntent().getIntExtra(EXTRA_CONTACT_ID, -1);

        if (contactId != -1) {
            buttonSave.setText(R.string.action_update);

            try (DBHelper dbHelper = new DBHelper(this)) {
                Contact contact = dbHelper.getContactById(contactId);
                editFirstName.setText(contact.getFirstName());
                editLastName.setText(contact.getLastName());
                editPhoneNumber.setText(contact.getPhoneNumber());
                editEmail.setText(contact.getEmail());
                editBirthday.setText(contact.getBirthday());
            }
        }

        buttonSave.setOnClickListener(v -> saveContact());

        registerColorableViews(buttonSave);
    }

    private void saveContact() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String birthday = editBirthday.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_required_fields),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!birthday.isEmpty() && !isValidBirthday(birthday)) {
            Toast.makeText(this, getString(R.string.error_invalid_birthday),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Contact contact = new Contact(firstName, lastName, phoneNumber, email, birthday);

        try (DBHelper dbHelper = new DBHelper(this)) {
            if (contactId == -1) {
                dbHelper.insertContact(contact);
            } else {
                contact.setId(contactId);
                dbHelper.updateContact(contact);
            }
        }

        // Close this activity and automatically go back to previous screen (MainActivity)
        // (equivalent to "Return" button).
        finish();
    }

    // SimpleDateFormat's "yyyy"/"dd"/"MM" pattern letters only set a *minimum* digit count when
    // parsing, not a maximum, so without this the field happily consumes extra digits (e.g.
    // "11/11/19111" parses as year 19111). Enforcing the exact digit shape first catches that.
    private static final Pattern BIRTHDAY_SHAPE = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

    private static boolean isValidBirthday(String birthday) {
        if (!BIRTHDAY_SHAPE.matcher(birthday).matches()) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setLenient(false);

        ParsePosition position = new ParsePosition(0);
        Date parsedDate = dateFormat.parse(birthday, position);

        // parse() only fills in errorIndex on failure and otherwise stops silently at the
        // first unparsable character, so require the whole string to have been consumed
        // (rejects non-existent calendar dates like 31/02/2026).
        if (position.getErrorIndex() != -1 || position.getIndex() != birthday.length()) {
            return false;
        }

        // A birthday can't be in the future.
        assert parsedDate != null;
        return !parsedDate.after(new Date());
    }
}
