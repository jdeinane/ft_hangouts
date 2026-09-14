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

    // Lets an already-open ConversationActivity refresh immediately instead of only picking up
    // the new message the next time it resumes.
    public static final String ACTION_MESSAGE_RECEIVED = "com.jubaldo.fthangouts.MESSAGE_RECEIVED";
    public static final String EXTRA_CONTACT_ID = "contact_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            return;
        }

        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages == null || messages.length == 0) {
            return;
        }

        // A single SMS longer than one segment (very easy to hit with emoji, which encode as
        // multiple UTF-16 units each) arrives as several SmsMessage parts sharing the same
        // sender - they must be concatenated back into one message rather than saved separately.
        String senderNumber = messages[0].getOriginatingAddress();
        if (senderNumber == null) {
            return;
        }

        StringBuilder bodyBuilder = new StringBuilder();
        for (SmsMessage part : messages) {
            String partBody = part.getMessageBody();
            if (partBody != null) {
                bodyBuilder.append(partBody);
            }
        }
        String body = bodyBuilder.toString();

        if (body.isEmpty()) {
            return;
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

                Intent messageReceivedIntent = new Intent(ACTION_MESSAGE_RECEIVED);
                messageReceivedIntent.setPackage(context.getPackageName());
                messageReceivedIntent.putExtra(EXTRA_CONTACT_ID, contact.getId());
                context.sendBroadcast(messageReceivedIntent);
            }
        }
    }
}
