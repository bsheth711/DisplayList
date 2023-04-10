package com.displaylist.app;

import android.provider.BaseColumns;

/**
 * A class to define the database schema
 */
public final class DatabaseContract {
    // make constructor private to prevent mistaken initialization
    private DatabaseContract() {}

    /**
     * A class to define the items table schema
     */
    public static class ItemTable implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_LIST = "listId";
        public static final String COLUMN_NAME_NAME = "name";
    }
}
