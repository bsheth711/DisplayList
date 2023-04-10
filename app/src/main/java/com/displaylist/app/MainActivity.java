package com.displaylist.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import android.util.JsonReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;

import android.database.Cursor;
import android.util.Log;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DisplayList";

    /**
     * 1. Sets layout to activity_main
     * 2. calls DatabaseOperations.loadData() to connect to website and add data to database
     * 3. calls DatabaseOperations.queryDb() to extract required data from database
     * 4. calls populate() to populate table layout with required data
     * @param savedInstanceState the current UI state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //DatabaseOperations dBO = new DatabaseOperations();

        // spinning up a thread to preform network tasks and add data to the database
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    DatabaseOperations.loadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            // Continue once the thread has finished adding data to the database
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // now extract data from the database
        Cursor cur = DatabaseOperations.queryDb();

        // filling layout with data
        populate(cur);

        // releasing resources
        cur.close();
        DatabaseOperations.close();
    }


    @Override
    protected void onDestroy() {
        // attempt to close database--it may already have been closed
        try {
            DatabaseOperations.close();
        } catch (Exception ex) {}
        super.onDestroy();
    }

    /**
     * A private helper method to populate a layout with data
     * @param cur the Cursor over the data to be added
     */
    private void populate(Cursor cur) {
        LinearLayout table = findViewById(R.id.table);

        // adding each row of results from the database to the layout
        while (cur.moveToNext()) {
            int id = cur.getInt(cur.getColumnIndexOrThrow(DatabaseContract.ItemTable.COLUMN_NAME_ID));
            int listId = cur.getInt(cur.getColumnIndexOrThrow(DatabaseContract.ItemTable.COLUMN_NAME_LIST));
            String name = cur.getString(cur.getColumnIndexOrThrow(DatabaseContract.ItemTable.COLUMN_NAME_NAME));

            LinearLayout row = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.row, table, false);

            TextView idEntry = (TextView) LayoutInflater.from(this).inflate(R.layout.entry, row, false);
            idEntry.setText(String.valueOf(id));
            row.addView(idEntry);

            TextView listIdEntry = (TextView) LayoutInflater.from(this).inflate(R.layout.entry, row, false);
            listIdEntry.setText(String.valueOf(listId));
            row.addView(listIdEntry);

            TextView nameEntry = (TextView) LayoutInflater.from(this).inflate(R.layout.entry, row, false);
            nameEntry.setText(name);
            row.addView(nameEntry);

            table.addView(row);
        }
    }
}