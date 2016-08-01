package com.thyago.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.thyago.sunshine.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by thyago on 8/1/16.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_LOCATION = "99705";
    static final long TEST_DATE = 1419033600L;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, index == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + valueCursor.getString(index) + "' did not match the expected value '" + expectedValue + "' ." + error,
                    expectedValue, valueCursor.getString(index));
        }
    }

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues values = new ContentValues();
        values.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        values.put(WeatherContract.WeatherEntry.COLUMN_DATE, TEST_DATE);
        values.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        values.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
        values.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        values.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);
        return values;
    }

    static ContentValues createNorthPoleLocationValues() {
        ContentValues values = new ContentValues();

        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "North Pole");
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, 64.7488);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, -147.353);

        return values;
    }

    static long insertNorthPoleLocationValues(Context context) {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        long id;
        id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);

        assertTrue("Error: Failure to include data into the database.", id != -1);

        return id;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread handler) {
            super(new Handler(handler.getLooper()));
            mHT = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {

                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}