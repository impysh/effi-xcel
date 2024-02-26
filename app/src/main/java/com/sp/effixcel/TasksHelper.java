package com.sp.effixcel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TasksHelper extends SQLiteOpenHelper
{
    private static final int SCHEMA_VERSION = 1;
    private static final String DATABASE_NAME = "TaskDatabase";
    private static final String TASK_TABLE = "task_table";
    private static final String ID = "id";
    private static final String TASKNAME = "taskName";
    private static final String TASKDES = "taskDescription";
    private static final String COLOR = "color";
    private static final String STATUS = "status";
    // Add a new column for storing the user UID
    private static final String USER_UID = "userUid";

    // Modify the CREATE_TASK_TABLE statement to include the new column
    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASKNAME + " TEXT, " + TASKDES + " TEXT, " + STATUS + " INTEGER," + COLOR + " INTEGER," + USER_UID + " TEXT)";

    private SQLiteDatabase db;

    public TasksHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the older table
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        // Create table again
        onCreate(db);
    }

    public void openDatabase()
    {
        db = this.getWritableDatabase();
    }

    public void insertTask(String taskName, String taskDescription, int color) {
        ContentValues cv = new ContentValues();

        cv.put(TASKNAME, taskName);
        cv.put(TASKDES, taskDescription);
        cv.put(STATUS, 0);
        cv.put(COLOR, color);
        cv.put(USER_UID, FirebaseAuth.getInstance().getCurrentUser().getUid()); // Add the user UID

        db.insert(TASK_TABLE, null, cv);
    }

    // Read all records from tasks_table
    @SuppressLint("Range")
    public List<Task> getAll() {
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            cursor = db.query(TASK_TABLE, null, USER_UID + "=?", new String[]{currentUserUid}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Task task = new Task();
                        task.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                        task.setTaskName(cursor.getString(cursor.getColumnIndex(TASKNAME)));
                        task.setTaskDescription(cursor.getString(cursor.getColumnIndex(TASKDES)));
                        task.setColor(cursor.getInt(cursor.getColumnIndex(COLOR)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
                        taskList.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
        return taskList;
    }

    public void updateStatus(int id, int status)
    {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        cv.put(USER_UID, FirebaseAuth.getInstance().getCurrentUser().getUid()); // Add the user UID
        db.update(TASK_TABLE, cv, ID + "=? AND " + USER_UID + "=?",
                new String[]{String.valueOf(id), FirebaseAuth.getInstance().getCurrentUser().getUid()});

    }

    public void updateTask(int id, String taskName, String taskDescription, int color)
    {
        ContentValues cv = new ContentValues();

        cv.put(TASKNAME, taskName);
        cv.put(TASKDES, taskDescription);
        cv.put(COLOR, color);

        cv.put(USER_UID, FirebaseAuth.getInstance().getCurrentUser().getUid()); // Add the user UID
        db.update(TASK_TABLE, cv, ID + "=? AND " + USER_UID + "=?",
                new String[]{String.valueOf(id), FirebaseAuth.getInstance().getCurrentUser().getUid()});
    }

    public void deleteTask(int id) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.delete(TASK_TABLE, ID + "=? AND " + USER_UID + "=?",
                new String[]{String.valueOf(id), currentUserUid});
    }
}

