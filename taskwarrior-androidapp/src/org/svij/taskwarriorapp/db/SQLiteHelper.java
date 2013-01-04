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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_TASKS = "tasks";
	public static final String COLUMN_UUID = "_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DUEDATE = "duedate";
	public static final String COLUMN_ENTRY = "entry";
	public static final String COLUMN_PRIORITY = "priority";
	public static final String COLUMN_PROJECT = "project";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_END = "end_timestamp";
	public static final String COLUMN_TAGS = "tags";

	private static final String DATABASE_NAME = "tasks.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_TASKS
			+ "(" + COLUMN_UUID + " String PRIMARY KEY, "
			+ COLUMN_DESCRIPTION + " TEXT, " + COLUMN_DUEDATE + " LONG, "
			+ COLUMN_ENTRY + " LONG NOT NULL," + COLUMN_END + " LONG, "
			+ COLUMN_PRIORITY + " TEXT, " + COLUMN_PROJECT + " TEXT, "
			+ COLUMN_STATUS + " TEXT NOT NULL, " + COLUMN_TAGS + " TEXT);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
		onCreate(db);
	}
}