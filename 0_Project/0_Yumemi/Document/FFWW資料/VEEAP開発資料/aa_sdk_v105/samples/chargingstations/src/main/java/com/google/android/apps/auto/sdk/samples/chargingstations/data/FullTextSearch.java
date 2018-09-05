// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.apps.auto.sdk.samples.chargingstations.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FullTextSearch manages a SQLite database providing full text search capabilities for charging
 * stations.
 */
class FullTextSearch {
    private static final String TAG = "FullTextSearch";

    private static final String TABLE_NAME = "charging_stations";
    private static final String DOC_ID_COLUMN = "docid";
    private static final String TITLE_COLUMN = "title";
    private static final String ADDRESS_COLUMN = "full_address";

    private static final String[] CONTENT_COLUMNS = { DOC_ID_COLUMN };
    private static final String MATCH_CONTENT = TABLE_NAME + " MATCH ?";

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    FullTextSearch(@NonNull Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Finds charging stations containing a match for a string in their name or address. This
     * method may take a non-trivial amount of time to execute, so it should not be called in the
     * UI thread.
     *
     * @param prefix String to be matched. It is matched starting from beginning of words only.
     * @return list of station IDs that contain a match for the string
     */
    @WorkerThread
    List<Long> match(String prefix) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);

        String[] matchArg = new String[] { prefix + "*" };
        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                CONTENT_COLUMNS, MATCH_CONTENT, matchArg, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return Collections.emptyList();
        }
        int docIdColumn = cursor.getColumnIndex(DOC_ID_COLUMN);
        List<Long> stationIds = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            long stationId = cursor.getLong(docIdColumn);
            stationIds.add(stationId);
            cursor.moveToNext();
        }
        cursor.close();
        return stationIds;
    }

    /**
     * Closes the SQLite database used for full text search matching.
     */
    void close() {
        mDatabaseOpenHelper.close();
    }

    /**
     * Returns true if the database has been populated with data.
     */
    boolean isPopulated() {
        SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        boolean isPopulated = !mDatabaseOpenHelper.isTableEmpty(db);
        db.close();
        return isPopulated;
    }

    /**
     * Clears the existing database and writes the given station data into it. This method may
     * take a non-trivial amount of time to execute, so it should not be called in the UI thread.
     *
     * @param chargingStations charging station data to be written into the database
     */
    @WorkerThread
    void populateTable(List<ChargingStation> chargingStations) {
        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        mDatabaseOpenHelper.reset(db);
        for (ChargingStation station : chargingStations) {
            ContentValues rowData = new ContentValues();
            rowData.put(DOC_ID_COLUMN, station.id);
            rowData.put(TITLE_COLUMN, station.name);
            rowData.put(ADDRESS_COLUMN, station.getFullAddress());
            if (db.insert(TABLE_NAME, null, rowData) == -1) {
                Log.w(TAG, "Failed to insert: " + station.id + " " + station.name);
                break;
            }
        }
        db.close();
    }

    /**
     * Database helper class to deal with actual database calls.
     */
    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "charging_stations_db";
        private static final String CREATE_FTS_TABLE = "CREATE VIRTUAL TABLE " + TABLE_NAME
                + " USING fts3 (" + TITLE_COLUMN + ", " + ADDRESS_COLUMN + ")";
        private static final String DROP_FTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private static final String COUNT_ENTRIES = "SELECT COUNT(*) FROM " + TABLE_NAME;

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_FTS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            reset(db);
        }

        /**
         * Clears the content of the database, and creates an empty FTS table in it.
         *
         * @param db database to be cleared
         */
        void reset(SQLiteDatabase db) {
            db.execSQL(DROP_FTS_TABLE);
            onCreate(db);
        }

        /**
         * Returns true if the charging stations FTS table in a given database is empty.
         */
        boolean isTableEmpty(SQLiteDatabase db) {
            Cursor cursor = db.rawQuery(COUNT_ENTRIES, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                return true;
            }
            boolean isEmpty = (cursor.getInt(0) == 0);
            cursor.close();
            return isEmpty;
        }
    }
}
