package com.jubaldo.fthangouts.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
    Class that handles ft_hangout's SQLite database.
    Inherits of SQLiteOpenHelper, a class provided by Android SDK that handles
    creation and updates of a local database stored in the cellphone.
    Defines these two table's structure:
        - contacts (with the five mandatory fields)
        - messages (linked to a contact via a FK, to store content, timestamp and direction)

    Unique entry point to data persistence.
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

    /*
        DBHelper's constructor.
        Context is the environment in which the app runs and transmits it to SQLiteOpenHelper's constructor
        via super(...).
        Mandatory crossing point to create a DBHelper instance correctly configured with the database's
        name and its version.
    */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
        onCreate() executes `CREATE TABLE` SQL queries for the app's first launch ever.
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    /*
        onUpgrade() handles the case where the database's structure changes in the future.
        Automatically called if DATABASE_VERSION is changed.
        For this project, we delete and recreate the tables, but in prod, we would use
        `ALTER TABLE` to properly migrate.
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }
}
