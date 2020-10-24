package com.example.tasklistapp.dbutils;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.tasklistapp.model.Task;

import java.util.Date;

public class TaskCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Task getTask() {
        String id = getString((getColumnIndex(TaskDbTable.Cols.COLUMN_NAME_UUID)));
        String title = getString(getColumnIndex(TaskDbTable.Cols.COLUMN_NAME_TITLE));
        String description = getString(getColumnIndex(TaskDbTable.Cols.COLUMN_NAME_DESCRIPTION));
        long deadline = getLong(getColumnIndex(TaskDbTable.Cols.COLUMN_NAME_DEADLINE));
        long creationDate = getLong(getColumnIndex(TaskDbTable.Cols.COLUMN_NAME_CREATION_DATE));
        int isDone = getInt(getColumnIndex(TaskDbTable.Cols.COLUMN_NAME_IS_DONE));

        Task task = new Task(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(new Date(deadline));
        task.setCreationDate(new Date(creationDate));
        task.setDone(isDone != 0);

        return task;
    }
}
