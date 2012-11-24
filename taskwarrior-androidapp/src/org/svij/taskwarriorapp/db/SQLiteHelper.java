package org.svij.taskwarriorapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_TASKS = "tasks";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DUEDATE = "duedate";
	public static final String COLUMN_ENTRY = "entry";
	public static final String COLUMN_PRIORITY = "priority";
	public static final String COLUMN_PROJECT = "project";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_END = "end_timestamp";

	private static final String DATABASE_NAME = "tasks.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_TASKS
			+ "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_DESCRIPTION + " TEXT, " + COLUMN_DUEDATE + " TEXT, "
			+ COLUMN_ENTRY + " LONG NOT NULL," + COLUMN_END + " LONG, "
			+ COLUMN_PRIORITY + " TEXT, " + COLUMN_PROJECT + " TEXT, "
			+ COLUMN_STATUS + " TEXT NOT NULL);";

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