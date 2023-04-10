package com.displaylist.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    // set name to null to create in memory only database
    public static final String DATABASE_NAME = null;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.ItemTable.TABLE_NAME + " ("
                    + DatabaseContract.ItemTable.COLUMN_NAME_ID + " INTEGER,"
                    + DatabaseContract.ItemTable.COLUMN_NAME_LIST + " INTEGER,"
                    + DatabaseContract.ItemTable.COLUMN_NAME_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.ItemTable.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
