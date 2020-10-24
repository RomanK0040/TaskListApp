package com.example.tasklistapp.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tasklistapp.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskDbUtils {
    private static TaskDbUtils sTaskDbUtils;
    private SQLiteDatabase mDatabase;

    public static TaskDbUtils getUtils(Context context) {
        if(sTaskDbUtils == null) {
            sTaskDbUtils = new TaskDbUtils(context);
        }
        return sTaskDbUtils;
    }

    private TaskDbUtils(Context context) {
        mDatabase = new TaskDbHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public long addNewTask(Task task) {
        ContentValues values = getContentValues(task);
        //return id of the new task if successful
        return mDatabase.insert(TaskDbTable.TABLE_NAME, null, values);
    }

    public int updateTask(Task task) {
        String queryId = task.getId();
        ContentValues values = getContentValues(task);
        return mDatabase.update(
                TaskDbTable.TABLE_NAME,
                values,
                TaskDbTable.Cols.COLUMN_NAME_UUID + " = ?",
                new String[] {queryId});
    }

    public int deleteTask(Task task) {
        String id = task.getId();
        return mDatabase.delete(TaskDbTable.TABLE_NAME, TaskDbTable.Cols.COLUMN_NAME_UUID + " = ?", new String[] {id});
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try (TaskCursorWrapper cursor = queryTasks(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        }
        return tasks;
    }

    public List<Task> getActiveTasks() {
        List<Task> tasks = new ArrayList<>();

        try (TaskCursorWrapper cursor = queryTasks(TaskDbTable.Cols.COLUMN_NAME_IS_DONE + " = ?", new String[] {"0"})) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        }

        return tasks;
    }

    public List<Task> getTodayTasks() {
        List<Task> tasks = new ArrayList<>();
        Calendar today =Calendar.getInstance();

        try (TaskCursorWrapper cursor = queryTasks(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Calendar date = Calendar.getInstance();
                date.setTime(cursor.getTask().getDeadline());
                if (
                        today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                        today.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
                ) {
                    tasks.add(cursor.getTask());
                }
                cursor.moveToNext();
            }
        }

        return tasks;
    }


    public Task getTask(String id) {
        try (TaskCursorWrapper cursor =
                     queryTasks(TaskDbTable.Cols.COLUMN_NAME_UUID + " = ?", new String[] {id})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTask();
        }
    }

    private ContentValues getContentValues(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskDbTable.Cols.COLUMN_NAME_UUID, task.getId());
        values.put(TaskDbTable.Cols.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskDbTable.Cols.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskDbTable.Cols.COLUMN_NAME_DEADLINE, task.getDeadline().getTime());
        values.put(TaskDbTable.Cols.COLUMN_NAME_CREATION_DATE, task.getCreationDate().getTime());
        values.put(TaskDbTable.Cols.COLUMN_NAME_IS_DONE, task.isDone() ? 1 : 0);

        return values;
    }
    private TaskCursorWrapper queryTasks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TaskDbTable.TABLE_NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new TaskCursorWrapper(cursor);
    }

}
