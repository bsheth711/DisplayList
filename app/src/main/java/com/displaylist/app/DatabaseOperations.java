package com.displaylist.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * A 'static' top level class containing a database and database operations.
 */
public final class DatabaseOperations {
    private static String TAG = "DatabaseOperations";
    static SQLiteOpenHelper dbHelper;

    // private constructor to prevent instantiation
    private DatabaseOperations(){}

    static void close() {
        dbHelper.close();
    }

    /**
     * A helper method to load data from a url into an in memory database
     */
    static void loadData() {
        // create in memory database
        dbHelper = new DatabaseHelper(null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        URL url = null;
        InputStream urlStream = null;
        String connectTo= "https://fetch-hiring.s3.amazonaws.com/hiring.json";

        // attempt to connect to the website 5 times

        try {
            url = new URL(connectTo);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return;
        }

        for (int i = 0; i < 5; ++i) {
            try {
                urlStream = url.openStream();
                break;
            }
            catch (UnknownHostException ex) {
                Log.e(TAG, "loadData: Unable to connect to website. Trying Again...");

                // waiting for 100ms before trying again in case something has changed
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // give up if we are unable to connect after 5 times
        if (urlStream == null) {
            Log.e(TAG, "loadData: Unable to connect to website. Please check network. Exiting.");
            return;
        }

        // open JsonReader over data
        try (Reader stream = new InputStreamReader(urlStream);
             JsonReader reader = new JsonReader(stream)
        ) {

            reader.beginArray();
            // inserting each row into the database
            while (reader.hasNext()) {
                reader.beginObject();

                ContentValues values = new ContentValues();
                reader.nextName();
                values.put(DatabaseContract.ItemTable.COLUMN_NAME_ID, reader.nextInt());
                reader.nextName();
                values.put(DatabaseContract.ItemTable.COLUMN_NAME_LIST, reader.nextInt());
                reader.nextName();

                // since the name field can be null we must first check for a null and then read a
                // string if it is not null
                try {
                    reader.nextNull();
                    values.put(DatabaseContract.ItemTable.COLUMN_NAME_NAME, (String) null);
                }
                catch (IllegalStateException ex2) {
                    values.put(DatabaseContract.ItemTable.COLUMN_NAME_NAME, reader.nextString());
                }

                db.insert(DatabaseContract.ItemTable.TABLE_NAME, null, values);

                reader.endObject();
            }
            reader.endArray();

        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        finally {
            // attempt to close the urlStream
            try {
                urlStream.close();
            } catch (IOException e) {}
        }
    }

    /**
     * A helper class to execute a query on the database
     * @return A Cursor over the results of the query
     */
    static Cursor queryDb() {
        /*
        SELECT *
        FROM items
        WHERE name is not null and name != ''
        ORDER BY listId, name
         */

        String table = DatabaseContract.ItemTable.TABLE_NAME;
        String[] columns = null;
        String selection = DatabaseContract.ItemTable.COLUMN_NAME_NAME + " is not null and " +
                DatabaseContract.ItemTable.COLUMN_NAME_NAME + " != ''";
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = DatabaseContract.ItemTable.COLUMN_NAME_LIST + ", " +
                DatabaseContract.ItemTable.COLUMN_NAME_NAME;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
}
