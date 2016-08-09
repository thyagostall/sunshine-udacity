package com.thyago.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.thyago.sunshine.data.WeatherContract;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by thyago on 8/8/16.
 */
public class ForecastAdapter extends CursorAdapter {

    private Context mContext;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    private String getString(int id) {
        return mContext.getString(id);
    }

    private double getMetric(double t) {
        return t;
    }

    private double getImperial(double t) {
        return (t * 1.8) + 32;
    }

    private String formatHighLows(double high, double low, String unit) {
        if (getString(R.string.pref_value_metric).equals(unit)) {
            high = getMetric(high);
            low = getMetric(low);
        } else {
            high = getImperial(high);
            low = getImperial(low);
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        return String.format(Locale.getDefault(), "%d/%d", roundedHigh, roundedLow);
    }

    private String getPreferredUnit() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(getString(R.string.pref_key_unit), getString(R.string.pref_value_metric));
    }

    private String getReadableDateString(long time) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        String unit = getPreferredUnit();
        String result;

        String highAndLow = formatHighLows(
                cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)),
                cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)),
                unit
        );
        result = getReadableDateString(cursor.getLong(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE))) +
                " - " + cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)) +
                " - " + highAndLow;
        return result;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view;
        textView.setText(convertCursorRowToUXFormat(cursor));
    }
}
