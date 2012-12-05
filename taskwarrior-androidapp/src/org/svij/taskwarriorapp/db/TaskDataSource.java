package org.svij.taskwarriorapp.db;

import java.util.ArrayList;

import org.svij.taskwarriorapp.data.Task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskDataSource {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_DESCRIPTION, SQLiteHelper.COLUMN_DUEDATE,
			SQLiteHelper.COLUMN_ENTRY, SQLiteHelper.COLUMN_STATUS,
			SQLiteHelper.COLUMN_END, SQLiteHelper.COLUMN_PROJECT,
			SQLiteHelper.COLUMN_PRIORITY };
	private String[] projectColumn = { SQLiteHelper.COLUMN_PROJECT };

	public TaskDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createTask(String task_description, String date, String status,
			String project, String priority) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, task_description);
		values.put(SQLiteHelper.COLUMN_DUEDATE, date);
		values.put(SQLiteHelper.COLUMN_ENTRY, System.currentTimeMillis() / 1000);
		values.put(SQLiteHelper.COLUMN_STATUS, status);
		values.put(SQLiteHelper.COLUMN_PROJECT, project);
		values.put(SQLiteHelper.COLUMN_PRIORITY, priority);
		database.insert(SQLiteHelper.TABLE_TASKS, null, values);
	}

	public void editTask(long id, String task_description, String date,
			String status, String project, String priority) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_ID, id);
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, task_description);
		values.put(SQLiteHelper.COLUMN_DUEDATE, date);
		values.put(SQLiteHelper.COLUMN_STATUS, status);
		values.put(SQLiteHelper.COLUMN_PROJECT, project);
		values.put(SQLiteHelper.COLUMN_PRIORITY, priority);
		database.update(SQLiteHelper.TABLE_TASKS, values,
				SQLiteHelper.COLUMN_ID + " = " + id, null);
		values = null;
	}

	public void deleteTask(long id) {
		Log.i("Deleted:", "Task with id: " + id);
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_STATUS, "deleted");
		database.update(SQLiteHelper.TABLE_TASKS, values,
				SQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public void doneTask(long id) {
		Log.i("Done:", "Task with id: " + id);
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_STATUS, "done");
		values.put(SQLiteHelper.COLUMN_END, System.currentTimeMillis() / 1000);
		Log.i("DoneTime:", ":" + System.currentTimeMillis() / 1000);
		database.update(SQLiteHelper.TABLE_TASKS, values,
				SQLiteHelper.COLUMN_ID + " = " + id, null);
		values = null;
	}

	public ArrayList<Task> getAllTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor, allColumns);
			tasks.add(task);
			cursor.moveToNext();
		}

		cursor.close();
		return tasks;
	}

	public ArrayList<Task> getPendingTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				SQLiteHelper.COLUMN_STATUS + " = 'pending'", null, null, null,
				null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor, allColumns);
			tasks.add(task);
			cursor.moveToNext();
		}

		cursor.close();
		return tasks;
	}

	public ArrayList<Task> getProjects() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(true, SQLiteHelper.TABLE_TASKS,
				projectColumn, SQLiteHelper.COLUMN_STATUS + " = 'pending'",
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor, projectColumn);
			tasks.add(task);
			cursor.moveToNext();
		}

		cursor.close();
		return tasks;
	}

	public ArrayList<Task> getProjectsTasks(String project) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				SQLiteHelper.COLUMN_PROJECT + "= '" + project + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor, allColumns);
			tasks.add(task);
			cursor.moveToNext();
		}

		cursor.close();
		return tasks;
	}

	public Task getTask(long id) {
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				SQLiteHelper.COLUMN_ID + "=" + id, null, null, null, null);
		cursor.moveToFirst();
		Task task = cursorToTask(cursor, allColumns);
		cursor.close();
		return task;
	}

	private Task cursorToTask(Cursor cursor, String[] columns) {
		Task task = new Task();

		if (columns.equals(projectColumn)) {
			task.setProject(cursor.getString(0));
		} else {
			task.setId(cursor.getLong(0));
			task.setDescription(cursor.getString(1));
			task.setDuedate(cursor.getString(2));
			task.setEntry(cursor.getLong(3));
			task.setStatus(cursor.getString(4));
			task.setEnd(cursor.getLong(5));
			task.setProject(cursor.getString(6));
			task.setPriority(cursor.getString(7));
		}

		return task;
	}
}
