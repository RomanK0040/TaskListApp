package com.example.tasklistapp.dbutils;

import android.provider.BaseColumns;

public final class TaskDbTable {
    public static final String TABLE_NAME = "tasks";

    public static class Cols implements BaseColumns {
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DEADLINE = "deadline";
        public static final String COLUMN_NAME_CREATION_DATE = "creationDate";
        public static final String COLUMN_NAME_IS_DONE = "isDone";
    }
}
