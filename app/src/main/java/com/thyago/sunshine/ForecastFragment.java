package com.thyago.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by thyago on 7/25/16.
 */
public class ForecastFragment extends Fragment {
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> mAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_forecast_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
        }
        return true;
    }

    private void updateWeather() {
        String postalCode = SunshinePrefs.getPreferredLocation();
        new ForecastWeatherTask().execute(postalCode);
    }

    private String getPreferredUnit() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getString(getString(R.string.pref_key_unit), getString(R.string.pref_value_metric));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<String> fakeData = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, fakeData);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
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

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {
        final String TAG_LIST = "list";
        final String TAG_WEATHER = "weather";
        final String TAG_TEMPERATURE = "temp";
        final String TAG_MAX = "max";
        final String TAG_MIN = "min";
        final String TAG_DESCRIPTION = "main";

        JSONObject jForecast = new JSONObject(forecastJsonStr);
        JSONArray jWeathers = jForecast.getJSONArray(TAG_LIST);

        Date dayTime = new Date();
        String[] results = new String[numDays];
        String unit = getPreferredUnit();

        for (int i = 0; i < jWeathers.length(); i++) {
            String day;
            String description;
            String highAndLow;

            JSONObject jDayForecast = jWeathers.getJSONObject(i);

            long dateTime;
            dateTime = dayTime.getTime();
            day = getReadableDateString(dateTime);

            JSONObject jWeather = jDayForecast.getJSONArray(TAG_WEATHER).getJSONObject(0);
            description = jWeather.getString(TAG_DESCRIPTION);

            JSONObject jTemperature = jDayForecast.getJSONObject(TAG_TEMPERATURE);
            double high = jTemperature.getDouble(TAG_MAX);
            double low = jTemperature.getDouble(TAG_MIN);

            highAndLow = formatHighLows(high, low, unit);
            results[i] = day + " - " + description + " - " + highAndLow;
        }

        return results;
    }

    private class ForecastWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                String postalCode = params[0];
                int numDays = 7;

                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("api.openweathermap.org")
                        .path("data/2.5/forecast/daily")
                        .appendQueryParameter("q", postalCode)
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
                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append('\n');
                }

                if (buffer.length() == 0) return null;

                return getWeatherDataFromJson(buffer.toString(), numDays);
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
    }

}
