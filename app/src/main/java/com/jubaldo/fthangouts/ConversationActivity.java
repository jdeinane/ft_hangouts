package com.jubaldo.fthangouts;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jubaldo.fthangouts.adapter.MessageAdapter;
import com.jubaldo.fthangouts.db.DBHelper;
import com.jubaldo.fthangouts.model.Contact;
import com.jubaldo.fthangouts.model.Message;
import com.jubaldo.fthangouts.receiver.SmsReceiver;

import java.util.List;

/**
 * Screen displaying the SMS conversation thread with a specific contact.
 * Loads message history from SQLite database and displays it in a RecyclerView.
 * Handles sending new SMS messages via SmsManager and saving them to the database.
 */
public class ConversationActivity extends BaseActivity {

    public static final String EXTRA_CONTACT_ID = "contact_id";

    private RecyclerView recyclerView;
    private EditText editMessageBody;
    private Contact contact;
    private int contactId;

    // Refreshes the thread immediately when a new message for this contact arrives while the
    // conversation is already open, instead of only picking it up on the next onResume().
    private final BroadcastReceiver messageReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedContactId = intent.getIntExtra(SmsReceiver.EXTRA_CONTACT_ID, -1);
            if (receivedContactId == contactId) {
                loadContactAndMessages();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        setupToolbar();

        contactId = getIntent().getIntExtra(EXTRA_CONTACT_ID, -1);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editMessageBody = findViewById(R.id.editMessageBody);

        View buttonSendMessage = findViewById(R.id.buttonSendMessage);
        buttonSendMessage.setOnClickListener(v -> sendMessage());

        registerColorableViews(buttonSendMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContactAndMessages();

        IntentFilter filter = new IntentFilter(SmsReceiver.ACTION_MESSAGE_RECEIVED);
        ContextCompat.registerReceiver(this, messageReceivedReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceivedReceiver);
    }

    private void loadContactAndMessages() {
        List<Message> messages;
        try (DBHelper dbHelper = new DBHelper(this)) {
            contact = dbHelper.getContactById(contactId);
            messages = dbHelper.getMessagesForContact(contactId);
        }

        if (contact == null) {
            finish();
            return;
        }

        String contactDisplayName = getString(R.string.format_full_name, contact.getFirstName(), contact.getLastName());
        setTitle(contactDisplayName);

        recyclerView.setAdapter(new MessageAdapter(messages, contactDisplayName));
        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    private void sendMessage() {
        String body = editMessageBody.getText().toString();

        if (body.isEmpty()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.error_sms_permission_denied), Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(contact.getPhoneNumber(), null, body, null, null);

        Message message = new Message(contactId, body, System.currentTimeMillis(), Message.TYPE_SENT);
        try (DBHelper dbHelper = new DBHelper(this)) {
            dbHelper.insertMessage(message);
        }

        editMessageBody.setText("");
        loadContactAndMessages();
    }
}
