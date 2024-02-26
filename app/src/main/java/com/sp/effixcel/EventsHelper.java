package com.sp.effixcel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class EventsHelper extends SQLiteOpenHelper {
    private static EventsHelper EventDB;
    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Event";

    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String DATE_FIELD = "date"; // New field for date
    private static final String TIME_FIELD = "time"; // New field for time
    private static final String DELETED_FIELD = "deleted";
    private static final String COLOR_FIELD = "color";
    private static final String ALARM_TIME_FIELD = "alarm_time";
    private static final String USER_UID_FIELD = "userUid";

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    // Add a primary key field to the table
    private static final String PRIMARY_KEY_FIELD = "PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            ID_FIELD + " INTEGER " + PRIMARY_KEY_FIELD + ", " + NAME_FIELD + " TEXT, " + DATE_FIELD + " TEXT, " + TIME_FIELD + " TEXT, " +
            DELETED_FIELD + " TEXT, " + COLOR_FIELD + " INTEGER, " + ALARM_TIME_FIELD + " TEXT, " + USER_UID_FIELD + " TEXT)";

    public EventsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static EventsHelper instanceOfDatabase(Context context) {
        if (EventDB == null)
            EventDB = new EventsHelper(context);

        return EventDB;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Perform necessary database schema upgrades
    }

    public void addEventToDatabase(Event event) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_FIELD, event.getName());
        contentValues.put(DATE_FIELD, event.getDate().toString());
        contentValues.put(TIME_FIELD, event.getTime().toString());
        contentValues.put(DELETED_FIELD, getStringFromDate(event.getDeleted()));
        contentValues.put(COLOR_FIELD, event.getColor());

        contentValues.put(USER_UID_FIELD, FirebaseAuth.getInstance().getCurrentUser().getUid()); // Add the user UID

        // Check if the alarmTime is not null before converting LocalTime to String
        if (event.getAlarmTime() != null) {
            contentValues.put(ALARM_TIME_FIELD, event.getAlarmTime().toString()); // Convert LocalTime to String
        } else {
            contentValues.putNull(ALARM_TIME_FIELD); // Set alarmTime as null in the database
        }

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }


    public void updateEventInDB(Event event) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_FIELD, event.getName());
        contentValues.put(DATE_FIELD, event.getDate().toString());
        contentValues.put(TIME_FIELD, event.getTime().toString());
        contentValues.put(DELETED_FIELD, getStringFromDate(event.getDeleted()));
        contentValues.put(COLOR_FIELD, event.getColor());

        // Check if the alarmTime is not null before converting LocalTime to String
        if (event.getAlarmTime() != null) {
            contentValues.put(ALARM_TIME_FIELD, event.getAlarmTime().toString()); // Convert LocalTime to String
        } else {
            contentValues.putNull(ALARM_TIME_FIELD); // Set alarmTime as null in the database
        }
        contentValues.put(USER_UID_FIELD, FirebaseAuth.getInstance().getCurrentUser().getUid()); // Add the user UID

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD + " =? AND " + USER_UID_FIELD + " =? ",
                new String[]{String.valueOf(event.getId()), FirebaseAuth.getInstance().getCurrentUser().getUid()});
    }


    /*
    public void populateEventListArray() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Event.eventArrayList.clear(); // IMPORTANT!!! Clear the existing events before loading from the database

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    int idIndex = result.getColumnIndex(ID_FIELD);
                    int nameIndex = result.getColumnIndex(NAME_FIELD);
                    int dateIndex = result.getColumnIndex(DATE_FIELD); // Index of the date field in the cursor
                    int timeIndex = result.getColumnIndex(TIME_FIELD); // Index of the time field in the cursor
                    int deletedIndex = result.getColumnIndex(DELETED_FIELD);
                    int colorIndex = result.getColumnIndex(COLOR_FIELD);
                    int alarmIndex = result.getColumnIndex(ALARM_TIME_FIELD); // Index of the alarm time field in the cursor

                    int id = result.getInt(idIndex);
                    String name = result.getString(nameIndex);
                    String dateString = result.getString(dateIndex);
                    String timeString = result.getString(timeIndex);
                    String stringDeleted = result.getString(deletedIndex);
                    Date deleted = getDateFromString(stringDeleted);
                    int color = result.getInt(colorIndex);
                    String stringAlarmTime = result.getString(alarmIndex);
                    LocalTime alarmTime = LocalTime.parse(stringAlarmTime); // Convert alarmTime string to LocalTime

                    // Convert date and time strings to LocalDate and LocalTime objects
                    LocalDate date = LocalDate.parse(dateString);
                    LocalTime time = LocalTime.parse(timeString);

                    Event event = new Event(id, name, date, time, deleted, color, alarmTime);
                    event.setAlarmTime(alarmTime); // Set the alarmTime

                    Event.eventArrayList.add(event);
                }
            }
        }
    }
    */

    public ArrayList<Event> eventsForDate(LocalDate date) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Event> events = new ArrayList<>();

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + DATE_FIELD + "=? AND " + USER_UID_FIELD + "=?",
                new String[]{date.toString(), currentUserUid})) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    int idIndex = result.getColumnIndex(ID_FIELD);
                    int nameIndex = result.getColumnIndex(NAME_FIELD);
                    int dateIndex = result.getColumnIndex(DATE_FIELD);
                    int timeIndex = result.getColumnIndex(TIME_FIELD);
                    int deletedIndex = result.getColumnIndex(DELETED_FIELD);
                    int colorIndex = result.getColumnIndex(COLOR_FIELD);
                    int alarmIndex = result.getColumnIndex(ALARM_TIME_FIELD); // Index of the alarm time field in the cursor

                    int id = result.getInt(idIndex);
                    String name = result.getString(nameIndex);
                    String dateString = result.getString(dateIndex);
                    String timeString = result.getString(timeIndex);
                    String stringDeleted = result.getString(deletedIndex);
                    Date deleted = getDateFromString(stringDeleted);
                    int color = result.getInt(colorIndex);

                    // Get the alarm time string from the cursor using the alarmIndex
                    String alarmTimeString = result.getString(alarmIndex);

                    // Convert date and time strings to LocalDate and LocalTime objects
                    LocalDate eventDate = LocalDate.parse(dateString);
                    LocalTime eventTime = LocalTime.parse(timeString);

                    // Convert the alarm time string to LocalTime object
                    LocalTime alarmTime = null;
                    if (alarmTimeString != null) {
                        alarmTime = LocalTime.parse(alarmTimeString);
                    }

                    Event event = new Event(id, name, eventDate, eventTime, deleted, color, alarmTime);
                    event.setAlarmTime(alarmTime); // Set the alarm time in the Event object

                    events.add(event);
                }
            }
        }

        return events;
    }

    public void deleteEventFromDB(int eventId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sqLiteDatabase.delete(TABLE_NAME, ID_FIELD + "=? AND " + USER_UID_FIELD + "=?",
                new String[]{String.valueOf(eventId), currentUserUid});
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