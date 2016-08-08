package com.thyago.sunshine.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.thyago.sunshine.Constants;
import com.thyago.sunshine.R;
import com.thyago.sunshine.SunshineApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by thyago on 8/4/16.
 */
public class ForecastWeatherTask extends AsyncTask<String, Void, String[]> {

    private static final String LOG_TAG = ForecastWeatherTask.class.getSimpleName();

    private final Context mContext;
    private final ArrayAdapter<String> mAdapter;

    public ForecastWeatherTask(ArrayAdapter<String> adapter) {
        mContext = SunshineApplication.getSunshineContext();
        mAdapter = adapter;
    }

    private String getAddLocationSelection() {
        return WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " = ? AND " +
                WeatherContract.LocationEntry.COLUMN_COORD_LAT + " = ? AND " +
                WeatherContract.LocationEntry.COLUMN_COORD_LONG + " = ?";
    }

    private String[] getAddLocationSelectionArgs(String locationSetting, String cityName, double lat, double lon) {
        return new String[] {
                locationSetting,
                cityName,
                String.valueOf(lat),
                String.valueOf(lon),
        };
    }

    private long insertLocation(String locationSetting, String cityName, double lat, double lon) {
        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

        Uri insertedUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, values);
        return ContentUris.parseId(insertedUri);
    }

    long addLocation(String locationSetting, String cityName, double lat, double lon) {
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                getAddLocationSelection(),
                getAddLocationSelectionArgs(locationSetting, cityName, lat, lon),
                null
        );
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        } else {
            return insertLocation(locationSetting, cityName, lat, lon);
        }
    }

    String[] convertContentValuesToUXFormat(Vector<ContentValues> values) {
        String[] result = new String[values.size()];
        String unit = getPreferredUnit();

        for (int i = 0; i < result.length; i++) {
            ContentValues item = values.elementAt(i);
            String highAndLow = formatHighLows(
                    item.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP),
                    item.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP),
                    unit
            );
            result[i] = getReadableDateString(item.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)) +
                    " - " + item.getAsString(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC) +
                    " - " + highAndLow;
        }
        return result;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            String locationQuery = params[0];
            int numDays = 14;

            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("api.openweathermap.org")
                    .path("data/2.5/forecast/daily")
                    .appendQueryParameter("q", locationQuery)
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("cnt", String.valueOf(numDays))
                    .appendQueryParameter("appid", Constants.API_KEY)
                    .build();
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }

            if (buffer.length() == 0) {
                return null;
            }
            return getWeatherDataFromJson(buffer.toString(), locationQuery);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getClass().getSimpleName(), e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing the stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String[] strings) {
        mAdapter.clear();
        mAdapter.addAll(strings);
    }


    private String[] getWeatherDataFromJson(String forecastJsonStr, String locationSetting) throws JSONException {
        final String TAG_CITY = "city";
        final String TAG_CITY_NAME = "name";
        final String TAG_COORD = "coord";

        final String TAG_LATITUDE = "lat";
        final String TAG_LONGITUDE = "lon";

        final String TAG_LIST = "list";

        final String TAG_PRESSURE = "pressure";
        final String TAG_HUMIDITY = "humidity";
        final String TAG_WINDSPEED = "speed";
        final String TAG_WIND_DIRECTION = "deg";

        final String TAG_TEMPERATURE = "temp";
        final String TAG_MAX = "max";
        final String TAG_MIN = "min";

        final String TAG_WEATHER = "weather";
        final String TAG_DESCRIPTION = "main";
        final String TAG_WEATHER_ID = "id";

        JSONObject jForecast = new JSONObject(forecastJsonStr);
        JSONArray jWeathers = jForecast.getJSONArray(TAG_LIST);

        JSONObject jCity = jForecast.getJSONObject(TAG_CITY);
        String cityName = jCity.getString(TAG_CITY_NAME);

        JSONObject jCityCoords = jCity.getJSONObject(TAG_COORD);
        double cityLatitude = jCityCoords.getDouble(TAG_LATITUDE);
        double cityLongitude = jCityCoords.getDouble(TAG_LONGITUDE);

        long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

        Vector<ContentValues> values = new Vector<>(jWeathers.length());

        Date dayTime = new Date();
        String unit = getPreferredUnit();

        for (int i = 0; i < jWeathers.length(); i++) {
            long dateTime;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            String description;
            int weatherId;

            JSONObject jDayForecast = jWeathers.getJSONObject(i);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dayTime);
            calendar.add(Calendar.DAY_OF_MONTH, i);
            dateTime = calendar.getTimeInMillis();

            pressure = jDayForecast.getDouble(TAG_PRESSURE);
            humidity = jDayForecast.getInt(TAG_HUMIDITY);
            windSpeed = jDayForecast.getDouble(TAG_WINDSPEED);
            windDirection = jDayForecast.getDouble(TAG_WIND_DIRECTION);

            JSONObject jWeather = jDayForecast.getJSONArray(TAG_WEATHER).getJSONObject(0);
            description = jWeather.getString(TAG_DESCRIPTION);
            weatherId = jWeather.getInt(TAG_WEATHER_ID);

            JSONObject jTemperature = jDayForecast.getJSONObject(TAG_TEMPERATURE);
            high = jTemperature.getDouble(TAG_MAX);
            low = jTemperature.getDouble(TAG_MIN);

            ContentValues item = new ContentValues();
            item.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
            item.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
            item.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            item.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            item.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            item.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            item.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            item.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            item.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            item.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            values.add(item);
        }

        if (values.size() > 0) {
            // Call bulk insert
        }

        String sortingOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, System.currentTimeMillis());

        // Display data stored right here

        return convertContentValuesToUXFormat(values);
    }


    private String getReadableDateString(long time) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
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

    private String getString(int id) {
        return mContext.getString(id);
    }
}
