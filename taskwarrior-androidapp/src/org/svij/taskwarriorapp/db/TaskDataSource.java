/**
 * taskwarrior for android – a task list manager
 *
 * Copyright (c) 2012 Sujeevan Vijayakumaran
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * allcopies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * http://www.opensource.org/licenses/mit-license.php
 *
 */

package org.svij.taskwarriorapp.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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
	private String[] allColumns = { SQLiteHelper.COLUMN_UUID,
			SQLiteHelper.COLUMN_DESCRIPTION, SQLiteHelper.COLUMN_DUEDATE,
			SQLiteHelper.COLUMN_ENTRY, SQLiteHelper.COLUMN_STATUS,
			SQLiteHelper.COLUMN_END, SQLiteHelper.COLUMN_PROJECT,
			SQLiteHelper.COLUMN_PRIORITY, SQLiteHelper.COLUMN_END,
			SQLiteHelper.COLUMN_TAGS};
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

	public void createTask(String task_description, long date, String status,
			String project, String priority, String tags) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_UUID, UUID.randomUUID().toString());
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, task_description);
		values.put(SQLiteHelper.COLUMN_DUEDATE, date);
		values.put(SQLiteHelper.COLUMN_ENTRY, System.currentTimeMillis() / 1000);
		values.put(SQLiteHelper.COLUMN_STATUS, status);
		values.put(SQLiteHelper.COLUMN_PROJECT, project);
		values.put(SQLiteHelper.COLUMN_PRIORITY, priority);
		values.put(SQLiteHelper.COLUMN_TAGS, tags);
		database.insert(SQLiteHelper.TABLE_TASKS, null, values);
	}

	public void editTask(UUID uuid, String task_description, long date,
			String status, String project, String priority, String tags) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_UUID, uuid.toString());
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, task_description);
		values.put(SQLiteHelper.COLUMN_DUEDATE, date);
		values.put(SQLiteHelper.COLUMN_STATUS, status);
		values.put(SQLiteHelper.COLUMN_PROJECT, project);
		values.put(SQLiteHelper.COLUMN_PRIORITY, priority);
		values.put(SQLiteHelper.COLUMN_TAGS, tags);
		database.update(SQLiteHelper.TABLE_TASKS, values,
				SQLiteHelper.COLUMN_UUID + " = '" + uuid.toString() + "'", null);
		values = null;
	}

	public void deleteTask(UUID uuid) {
		Log.i("Deleted:", "Task with uuid: " + uuid.toString());
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_STATUS, "deleted");
		database.update(SQLiteHelper.TABLE_TASKS, values,
				SQLiteHelper.COLUMN_UUID + " = '" + uuid.toString() + "'", null);
	}

	public void doneTask(UUID uuid) {
		Log.i("Done:", "Task with id: " + uuid.toString());
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_STATUS, "done");
		values.put(SQLiteHelper.COLUMN_END, System.currentTimeMillis() / 1000);
		Log.i("DoneTime:", ":" + System.currentTimeMillis() / 1000);
		database.update(SQLiteHelper.TABLE_TASKS, values,
				SQLiteHelper.COLUMN_UUID + " = '" + uuid.toString() + "'", null);
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

	public Cursor getProjectCursor() {
		Cursor cursor = database.query(true, SQLiteHelper.TABLE_TASKS,
				allColumns, SQLiteHelper.COLUMN_STATUS + " = 'pending'",
				null, null, null, null, null);

		cursor.moveToFirst();

		return cursor;
	}

	public ArrayList<Task> getProjectsTasks(String project) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				SQLiteHelper.COLUMN_PROJECT + "= '" + project + "' and "
						+ SQLiteHelper.COLUMN_STATUS + " = 'pending'", null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor, allColumns);
			tasks.add(task);
			cursor.moveToNext();
		}

		cursor.close();
		return tasks;
	}

	public Task getTask(UUID uuid) {
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				SQLiteHelper.COLUMN_UUID + "= '" + uuid.toString() + "'", null, null, null, null);
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
			task.setId(UUID.fromString(cursor.getString(0)));
			task.setDescription(cursor.getString(1));
			Date date = new Date(cursor.getLong(2));
			task.setDuedate(date);
			task.setEntry(cursor.getLong(3));
			task.setStatus(cursor.getString(4));
			task.setEnd(cursor.getLong(5));
			task.setProject(cursor.getString(6));
			task.setPriority(cursor.getString(7));
			task.setTags(cursor.getString(9));
			task.urgency_c();
		}

		return task;
	}
}
