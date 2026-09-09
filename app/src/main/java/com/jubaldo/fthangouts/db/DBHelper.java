package com.jubaldo.fthangouts.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jubaldo.fthangouts.model.Contact;
import com.jubaldo.fthangouts.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles ft_hangout's SQLite database.
 * Inherits of SQLiteOpenHelper, a class provided by Android SDK that handles
 * creation and updates of a local database stored in the cellphone.
 * Defines these two table's structure:
 *     - contacts (with the five mandatory fields)
 *     - messages (linked to a contact via a FK, to store content, timestamp and direction)
 *
 * Unique entry point to data persistence.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ft_hangouts.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_CONTACT_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_BIRTHDAY = "birthday";

    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_CONTACT_ID_FK = "contact_id";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_TYPE = "type";

    private static final String CREATE_TABLE_CONTACTS =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_PHONE_NUMBER + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_BIRTHDAY + " TEXT" +
                    ");";

    private static final String CREATE_TABLE_MESSAGES =
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTACT_ID_FK + " INTEGER, " +
                    COLUMN_BODY + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER, " +
                    COLUMN_TYPE + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_CONTACT_ID_FK + ") REFERENCES " + TABLE_CONTACTS + "(" + COLUMN_CONTACT_ID + ")" +
                    ");";

    /**
     * DBHelper's constructor.
     * Context is the environment in which the app runs and transmits it to SQLiteOpenHelper's constructor
     * via super(...).
     * Mandatory crossing point to create a DBHelper instance correctly configured with the database's
     * name and its version.
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * onCreate() executes `CREATE TABLE` SQL queries for the app's first launch ever.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    /**
     * onUpgrade() handles the case where the database's structure changes in the future.
     * Automatically called if DATABASE_VERSION is changed.
     * For this project, we delete and recreate the tables, but in prod, we would use
     * `ALTER TABLE` to properly migrate.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    /**
     * CRUD methods implementations for contacts:
     *     - create contact
     *     - read contact
     *     - update contact
     *     - delete contact
     */
    public long insertContact(Contact contact) {
        // getWritableDatabase(): request a write connection to the DB from SQLiteOpenHelper
        // Android checks if the .db file exists - else, automatically calls onCreate()
        SQLiteDatabase db = this.getWritableDatabase();

        // ContentValues(): key-value structure - associates a column name to a value.
        ContentValues values = new ContentValues();

        values.put(COLUMN_FIRST_NAME, contact.getFirstName());
        values.put(COLUMN_LAST_NAME, contact.getLastName());
        values.put(COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
        values.put(COLUMN_EMAIL, contact.getEmail());
        values.put(COLUMN_BIRTHDAY, contact.getBirthday());

        // db.insert(...): inserts a new line.
        long newId = db.insert(TABLE_CONTACTS, null, values);

        // db.close(): close the connection to the DB when the operation is finished.
        db.close();
        return newId;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor: result send by db.query(...), but not in a 'list' way.
        // It's an iterator that moves line by line in the result: it designates a position, not the whole content.
        Cursor cursor = db.query(
                TABLE_CONTACTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_LAST_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDAY))
                );
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)));
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contacts;
    }

    public Contact getContactById(int contactId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contact = null;

        Cursor cursor = db.query(
                TABLE_CONTACTS,
                null,
                COLUMN_CONTACT_ID + " = ?", // Unlike getAllContacts(), we retrieve here only one row.
                new String[]{String.valueOf(contactId)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            contact = new Contact(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDAY))
            );
            contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)));
        }

        cursor.close();
        db.close();
        return contact;
    }

    public Contact getContactByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contact = null;

        Cursor cursor = db.query(
                TABLE_CONTACTS,
                null,
                COLUMN_PHONE_NUMBER + " = ?",
                new String[]{phoneNumber},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            contact = new Contact(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDAY))
            );
            contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)));
        }

        cursor.close();
        db.close();
        return contact;

        // For now, phone numbers with country code (ex: "+33612345678") will not be considered
        // identical to "0612345678".
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, contact.getFirstName());
        values.put(COLUMN_LAST_NAME, contact.getLastName());
        values.put(COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
        values.put(COLUMN_EMAIL, contact.getEmail());
        values.put(COLUMN_BIRTHDAY, contact.getBirthday());

        int rowsAffected = db.update(
                TABLE_CONTACTS,
                values,
                COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );

        db.close();
        return rowsAffected;
    }

    public void deleteContact(int contactId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                TABLE_MESSAGES,
                COLUMN_CONTACT_ID_FK + " = ?",
                new String[]{String.valueOf(contactId)}
        );

        db.delete(
                TABLE_CONTACTS,
                COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contactId)}
        );

        db.close();
    }

    /**
     * CRUD methods implementations for messages
     */
    public long insertMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_ID_FK, message.getContactId());
        values.put(COLUMN_BODY, message.getBody());
        values.put(COLUMN_TIMESTAMP, message.getTimestamp());
        values.put(COLUMN_TYPE, message.getType());

        long newId = db.insert(TABLE_MESSAGES, null, values);
        db.close();
        return newId;
    }

    public List<Message> getMessagesForContact(int contactId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_MESSAGES,
                null,
                COLUMN_CONTACT_ID_FK + " = ?",
                new String[]{String.valueOf(contactId)},
                null,
                null,
                COLUMN_TIMESTAMP + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID_FK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BODY)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                        );
                message.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)));
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }
}
