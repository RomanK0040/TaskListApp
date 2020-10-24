package com.example.tasklistapp.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TaskDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tasks.db";

    public static final String SQL_CREATE_TASK = "CREATE TABLE " + TaskDbTable.TABLE_NAME + " (" +
            TaskDbTable.Cols._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TaskDbTable.Cols.COLUMN_NAME_UUID + " TEXT, " +
            TaskDbTable.Cols.COLUMN_NAME_TITLE + " TEXT, " +
            TaskDbTable.Cols.COLUMN_NAME_DESCRIPTION + " TEXT, " +
            TaskDbTable.Cols.COLUMN_NAME_DEADLINE + " INTEGER, " +
            TaskDbTable.Cols.COLUMN_NAME_CREATION_DATE + " INTEGER, " +
            TaskDbTable.Cols.COLUMN_NAME_IS_DONE + " INTEGER)";

    private static final String SQL_DELETE_TASK =
            "DROP TABLE IF EXISTS " + TaskDbTable.TABLE_NAME;

    public TaskDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TASK);
        onCreate(db);
    }
}
