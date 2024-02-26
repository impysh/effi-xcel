package com.sp.effixcel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotesHelper extends SQLiteOpenHelper {
    private static NotesHelper NotesDB;
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Note";

    private static final String ID_FIELD = "id";

    private static final String DESCRIPTION_FIELD = "desc";
    private static final String DELETED_FIELD = "deleted";
    private static final String COLOR_FIELD = "color";
    private static final String USER_UID_FIELD = "user_uid";

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    // Add a primary key field to the table
    private static final String PRIMARY_KEY_FIELD = "PRIMARY KEY AUTOINCREMENT";

    public NotesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static NotesHelper instanceOfDatabase(Context context) {
        if (NotesDB == null)
            NotesDB = new NotesHelper(context);

        return NotesDB;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(ID_FIELD)
                .append(" INTEGER ")
                .append(PRIMARY_KEY_FIELD)
                .append(", ")
                .append(DESCRIPTION_FIELD)
                .append(" TEXT, ")
                .append(DELETED_FIELD)
                .append(" TEXT, ")
                .append(COLOR_FIELD)
                .append(" INTEGER, ")
                .append(USER_UID_FIELD) // Add the column name here for user UID
                .append(" TEXT) ");


        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Perform necessary database schema upgrades
    }

    public void addNoteToDatabase(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPTION_FIELD, note.getDescription());
        contentValues.put(DELETED_FIELD, getStringFromDate(note.getDeleted()));
        contentValues.put(COLOR_FIELD, note.getColor());
        contentValues.put(USER_UID_FIELD, FirebaseAuth.getInstance().getCurrentUser().getUid()); // Add the user UID

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void updateNoteInDB(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPTION_FIELD, note.getDescription());
        contentValues.put(DELETED_FIELD, getStringFromDate(note.getDeleted()));
        contentValues.put(COLOR_FIELD, note.getColor());
        contentValues.put(USER_UID_FIELD, FirebaseAuth.getInstance().getCurrentUser().getUid()); // Add the user UID

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD + " =? ", new String[]{String.valueOf(note.getId())});
    }


    public void populateNoteListArray() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Note.noteArrayList.clear(); // IMPORTANT!!! Clear the existing notes before loading from the database

        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USER_UID_FIELD + "=?", new String[]{userUID})) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    int idIndex = result.getColumnIndex(ID_FIELD);
                    int descIndex = result.getColumnIndex(DESCRIPTION_FIELD);
                    int deletedIndex = result.getColumnIndex(DELETED_FIELD);
                    int colorIndex = result.getColumnIndex(COLOR_FIELD);

                    int id = result.getInt(idIndex);
                    String desc = result.getString(descIndex);
                    String stringDeleted = result.getString(deletedIndex);
                    Date deleted = getDateFromString(stringDeleted);
                    int color = result.getInt(colorIndex);

                    Note note = new Note(id, desc, deleted, color);

                    Note.noteArrayList.add(note);
                }
            }
        }
    }

    private String getStringFromDate(Date date) {
        if (date == null)
            return null;
        return dateFormat.format(date);
    }

    private Date getDateFromString(String string) {
        try {
            return dateFormat.parse(string);
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }
}