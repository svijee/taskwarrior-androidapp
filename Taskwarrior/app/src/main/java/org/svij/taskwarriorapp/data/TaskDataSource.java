package org.svij.taskwarriorapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.svij.taskwarriorapp.Task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskDataSource {

    private SQLiteDatabase database;
    private TaskDbHelper dbHelper;

    private String[] columns = {
            TaskDbHelper.COLUMN_ID,
            TaskDbHelper.COLUMN_UUID,
            TaskDbHelper.COLUMN_ENTRY,
            TaskDbHelper.COLUMN_STATUS,
            TaskDbHelper.COLUMN_DESCRIPTION,
            TaskDbHelper.COLUMN_PROJECT
    };

    public TaskDataSource(Context context) {
        dbHelper = new TaskDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_UUID, task.getUuid().toString());
        values.put(TaskDbHelper.COLUMN_ENTRY, task.getEntry().getTime());
        values.put(TaskDbHelper.COLUMN_STATUS, task.getStatus());
        values.put(TaskDbHelper.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskDbHelper.COLUMN_PROJECT, task.getProject());

        long insertId = database.insert(TaskDbHelper.TABLE_TASK, null, values);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<Task>();

        Cursor cursor = database.query(TaskDbHelper.TABLE_TASK, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Task task = getTask(cursor);
            taskList.add(task);
            cursor.moveToNext();
        }

        cursor.close();
        return taskList;
    }

    public Task getTask(Cursor cursor) {
        int uuidIndex = cursor.getColumnIndex(TaskDbHelper.COLUMN_UUID);
        int entryIndex = cursor.getColumnIndex(TaskDbHelper.COLUMN_ENTRY);
        int statusIndex = cursor.getColumnIndex(TaskDbHelper.COLUMN_STATUS);
        int descriptionIndex = cursor.getColumnIndex(TaskDbHelper.COLUMN_DESCRIPTION);
        int projectIndex = cursor.getColumnIndex(TaskDbHelper.COLUMN_PROJECT);

        Task task = new Task();
        task.setUUID(UUID.fromString(cursor.getString(uuidIndex)));
        task.setEntry(new Date(cursor.getLong(entryIndex)));
        task.setStatus(cursor.getString(statusIndex));
        task.setDescription(cursor.getString(descriptionIndex));
        task.setProject(cursor.getString(projectIndex));

        return task;
    }
}
