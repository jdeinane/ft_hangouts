package com.jubaldo.fthangouts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jubaldo.fthangouts.R;
import com.jubaldo.fthangouts.model.Contact;

import java.util.List;

/*
    Central piece of the RecyclerView system.
    Its role is to link the Contact list (data) and the screen views (ex: item_contact.xml layout), by row.
    An Adapter is an object that transforms each element of a list into a view to be displayed.
*/
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final List<Contact> contacts;

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    /*
        ContactViewHolder: 'box' or container that stores in memory, once for all,
        the references towards the two TextView, from a physical row to the screen.
        Used to avoid multiple calls of findViewById(...) each list scrolling (performance optimization).
    */
    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView textContactName;
        TextView textContactPhone;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textContactName = itemView.findViewById(R.id.textContactName);
            textContactPhone = itemView.findViewById(R.id.textContactPhone);
        }
    }
    
    /*
        Called by RecyclerView only when it needs a new row to display (not by each existing row,
        it recycles the views from the screen).
        LayoutInflater.from(...).inflate(...) transforms the XML file item_contact.xml to a real
        Java View object usable.
    */
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    /*
        Called to fill a row with the contact's data at the given position in the list.
    */
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        String fullName = contact.getFirstName() + " " + contact.getLastName();
        holder.textContactName.setText(fullName);
        holder.textContactPhone.setText(contact.getPhoneNumber());
    }

    /*
        Informs the RecyclerView the total number of rows so it knows how far to scroll.
    */
    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
