package org.svij.taskwarriorapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "task.db";
    public static final int DB_VERSION = 1;

    // Table Task
    public static final String TABLE_TASK = "task";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ENTRY = "entry";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_DUE = "due";
    public static final String COLUMN_UNTIL = "until";
    public static final String COLUMN_WAIT = "wait";
    public static final String COLUMN_MODIFIED = "modified";
    public static final String COLUMN_SCHEDULED = "scheduled";
    public static final String COLUMN_RECUR = "recur";
    public static final String COLUMN_MASK = "mask";
    public static final String COLUMN_IMASK = "imask";
    public static final String COLUMN_PARENT = "parent";
    public static final String COLUMN_PROJECT = "project";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_DEPENDS = "depends";

    // Table Annotation
    public static final String TABLE_ANNOTATIONS = "annotations";
    public static final String COLUMN_ANNOTATION_ID = "id";
    public static final String COLUMN_ANNOTATIONS_ENTRY = "entry";
    public static final String COLUMN_ANNOTATIONS_DESCRIPTION = "description";
    public static final String COLUMN_ANNOTATIONS_FK_TASK = "task";

    // Table Tags
    public static final String TABLE_TAGS = "tags";
    public static final String COLUMN_TAG_ID = "id";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_TAGS_FK_TASK = "task";

    // Table UDA
    public static final String TABLE_UDA = "uda";
    public static final String COLUMN_UDA_ID = "id";
    public static final String COLUMN_UDA = "attribute";
    public static final String COLUMN_UDA_FK_TASK = "task";


    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_TASK + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_UUID + " text not null, " +
                    COLUMN_STATUS + " text not null, " +
                    COLUMN_ENTRY + " text not null, " +
                    COLUMN_DESCRIPTION + " text not null, " +
                    COLUMN_START + " text, " +
                    COLUMN_END + " text, " +
                    COLUMN_DUE + " text, " +
                    COLUMN_UNTIL + " text, " +
                    COLUMN_WAIT + " text, " +
                    COLUMN_MODIFIED + " text, " +
                    COLUMN_SCHEDULED + " text, " +
                    COLUMN_RECUR + " text, " +
                    COLUMN_MASK + " text, " +
                    COLUMN_IMASK + " integer, " +
                    COLUMN_PARENT + " text, " +
                    COLUMN_PROJECT + " text, " +
                    COLUMN_PRIORITY + " text, " +
                    COLUMN_DEPENDS + " text); " +
            "CREATE TABLE " + TABLE_ANNOTATIONS + "(" +
                    COLUMN_ANNOTATION_ID + " integer primary key autoincrement, " +
                    COLUMN_ANNOTATIONS_DESCRIPTION + " text not null, " +
                    COLUMN_ANNOTATIONS_ENTRY + " text not null, " +
                    COLUMN_ANNOTATIONS_FK_TASK + " integer, " +
                    "FOREIGN KEY(" + COLUMN_ANNOTATIONS_FK_TASK + ") REFERENCES " + TABLE_TASK + "(" + COLUMN_ID + "); " +
            "CREATE TABLE " + TABLE_TAGS + "(" +
                    COLUMN_TAG_ID + " integer primary key autoincrement, " +
                    COLUMN_TAG + " text not null, " +
                    COLUMN_TAGS_FK_TASK + " integer, " +
                    "FOREIGN KEY(" + COLUMN_TAGS_FK_TASK + ") REFERENCES " + TABLE_TASK + "(" + COLUMN_ID + "); " +
            "CREATE TABLE " + TABLE_UDA + "(" +
                    COLUMN_UDA_ID + " integer primary key autoincrement, " +
                    COLUMN_UDA + " text, " +
                    COLUMN_UDA_FK_TASK + " integer, " +
                    "FOREIGN KEY(" + COLUMN_UDA_FK_TASK + ") REFERENCES " + TABLE_TASK + "(" + COLUMN_ID + "); ";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    public TaskDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
