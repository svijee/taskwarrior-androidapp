package org.svij.taskwarriorapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.svij.taskwarriorapp.Task;

public class TaskDataSource {

    private SQLiteDatabase database;
    private TaskDbHelper dbHelper;

    private String[] columns = {

    };

    public TaskDataSource(Context context) {
        dbHelper = new TaskDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }
}
