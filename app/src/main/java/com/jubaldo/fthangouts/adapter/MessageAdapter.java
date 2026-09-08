package com.jubaldo.fthangouts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jubaldo.fthangouts.R;
import com.jubaldo.fthangouts.model.Message;

import java.util.List;

/**
 * Adapter for the RecyclerView in ConversationActivity.
 * Binds a list of Message objects to the item_message.xml layout.
 * Displays who sent each message ("Me" or the contact's name) and the message body.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final String contactDisplayName;

    public MessageAdapter(List<Message> messages, String contactDisplayName) {
        this.messages = messages;
        this.contactDisplayName = contactDisplayName;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView textMessageSender;
        TextView textMessageBody;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageSender = itemView.findViewById(R.id.textMessageSender);
            textMessageBody = itemView.findViewById(R.id.textMessageBody);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        String sender = (message.getType() == Message.TYPE_SENT)
                ? holder.itemView.getContext().getString(R.string.label_me)
                : contactDisplayName;

        holder.textMessageSender.setText(sender);
        holder.textMessageBody.setText(message.getBody());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
