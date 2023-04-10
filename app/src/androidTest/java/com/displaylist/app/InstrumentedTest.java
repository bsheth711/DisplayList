package com.displaylist.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented tests, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    /**
     * Tests that DatabaseOperations.loadData() adds all (1000) rows of the JSON file to the
     * database
     */
    @Test
    public void checkNumInsertions() {
        DatabaseOperations.loadData();

        /*
        SELECT *
        FROM items
         */

        String table = DatabaseContract.ItemTable.TABLE_NAME;
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        SQLiteDatabase db = DatabaseOperations.dbHelper.getReadableDatabase();

        Cursor cur = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);

        assertEquals(1000, cur.getCount());
    }

    /**
     * Tests that the WHERE clause in DatabaseOperations.queryDb() is selecting only some of the
     * rows in the database
     */
    @Test
    public void checkNumResults() {
        DatabaseOperations.loadData();
        Cursor cur1 = DatabaseOperations.queryDb();

        /*
        SELECT *
        FROM items
        WHERE name is not null and name != ''
         */
        String table = DatabaseContract.ItemTable.TABLE_NAME;
        String[] columns = null;
        String selection = "name is not null and name != ''";
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        SQLiteDatabase db = DatabaseOperations.dbHelper.getReadableDatabase();

        Cursor cur2 = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);



        assertTrue(cur1.getCount() == cur2.getCount());
    }

    /**
     * Tests that there are no nulls or empty strings in the name column of results the Cursor from
     * DatabaseOperations.queryDb() iterates over
     */
    @Test
    public void checkNoNullsOrEmpty() {
        DatabaseOperations.loadData();
        Cursor cur = DatabaseOperations.queryDb();

        while (cur.moveToNext()) {
            String str = cur.getString(cur.getColumnIndexOrThrow(DatabaseContract.ItemTable.COLUMN_NAME_NAME));
            assertTrue(!str.equals("") || str != null);
        }

    }
}