package com.thyago.sunshine;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by thyago on 7/25/16.
 */
public class ForecastFragment extends Fragment {
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<String> fakeData = new ArrayList<>(7);
        fakeData.add("Today - Sunny - 88/63");
        fakeData.add("Tomorrow - Foggy - 70/46");
        fakeData.add("Wed - Cloudy - 72/63");
        fakeData.add("Thurs - Rainy - 64/51");
        fakeData.add("Fri - Foggy - 70/46");
        fakeData.add("Sat - Sunny - 76/68");
        fakeData.add("Sun - Sunny - 80/66");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, fakeData);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        new ForecastWeatherTask().execute();

        return rootView;
    }

    private static class ForecastWeatherTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=" + Constants.API_KEY);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append('\n');
                }

                if (buffer.length() == 0) return null;

                Log.d(LOG_TAG, buffer.toString());
                return buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getClass().getSimpleName(), e);
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
    }

}
