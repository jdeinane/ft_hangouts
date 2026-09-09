package com.jubaldo.fthangouts.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.jubaldo.fthangouts.R;
import com.jubaldo.fthangouts.db.DBHelper;
import com.jubaldo.fthangouts.model.Contact;
import com.jubaldo.fthangouts.model.Message;

/**
 * BroadcastReceiver triggered by the Android system when an incoming SMS is received.
 * Extracts the sender's phone number and message body, matches the sender to a contact
 * in the local SQLite database, saves the received message, and displays a Toast notification.
 * Unlike Activity, BroadcastReceiver isn't a screen: it's a composant that stays in standby,
 * ready to react when a specific event occurs.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages == null) {
            return;
        }

        for (SmsMessage smsMessage : messages) {
            String senderNumber = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody();

            if (senderNumber == null || body == null) {
                continue;
            }

            try (DBHelper dbHelper = new DBHelper(context)) {
                Contact contact = dbHelper.getContactByPhoneNumber(senderNumber);

                // Only receive SMS from saved contacts
                // in the DB: a message has to belong to an existent contact.
                if (contact != null) {
                    Message message = new Message(contact.getId(), body, System.currentTimeMillis(), Message.TYPE_RECEIVED);
                    dbHelper.insertMessage(message);

                    Toast.makeText(context, context.getString(R.string.format_new_message_toast, contact.getFirstName()),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
