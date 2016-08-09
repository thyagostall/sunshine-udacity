package com.thyago.sunshine;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import com.thyago.sunshine.data.ForecastWeatherTask;
import com.thyago.sunshine.data.WeatherContract;
import com.thyago.sunshine.preferences.SunshinePrefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thyago on 7/25/16.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FORECAST_LOADER = 0;
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ForecastAdapter mAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);

        mAdapter = new ForecastAdapter(getActivity(), null, 0);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = SunshinePrefs.getPreferredLocation();

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(), weatherForLocationUri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
