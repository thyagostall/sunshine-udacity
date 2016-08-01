package com.thyago.sunshine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by thyago on 8/1/16.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> set = new HashSet<>();
        set.add(WeatherContract.LocationEntry.TABLE_NAME);
        set.add(WeatherContract.WeatherEntry.TABLE_NAME);

        deleteTheDatabase();
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertTrue(db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: The database was not created correctly.", c.moveToFirst());

        int columnIndex = c.getColumnIndex("name");
        do {
            set.remove(c.getString(columnIndex));
        } while (c.moveToNext());
        assertTrue("Error: The database was not created with all the tables.", set.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")", null);
        c.moveToFirst();

        set.add(WeatherContract.LocationEntry._ID);
        set.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        set.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        set.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        set.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        columnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnIndex);
            set.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database does not contain all of the required fields.", set.isEmpty());
        db.close();
    }

    public void testLocationTable() {

    }

    public void testWeatherTable() {

    }

    public long insertLocation() {
        return -1L;
    }
}
