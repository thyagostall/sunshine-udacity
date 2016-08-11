package com.thyago.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.thyago.sunshine.preferences.SettingsActivity;
import com.thyago.sunshine.preferences.SunshinePrefs;

public class ForecastActivity extends AppCompatActivity {

    private static final String LOG_TAG = ForecastActivity.class.getSimpleName();
    private static final String FORECAST_FRAGMENT_TAG = ForecastFragment.class.getName();

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mLocation = SunshinePrefs.getPreferredLocation();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECAST_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mLocation.equals(SunshinePrefs.getPreferredLocation())) {
            return;
        }
        ForecastFragment fragment = (ForecastFragment) getFragmentManager().findFragmentByTag(FORECAST_FRAGMENT_TAG);
        fragment.onLocationChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                openSettingsActivity();
                return true;
            case R.id.action_view_preferred_location:
                openPreferredLocationMap();
                return true;
            default:
                return false;
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openPreferredLocationMap() {
        String location = SunshinePrefs.getPreferredLocation();
        Uri uri = Uri.parse("geo:0,0")
                .buildUpon()
                .appendQueryParameter("q", location)
                .build();
        Intent map = new Intent(Intent.ACTION_VIEW, uri);
        if (map.resolveActivity(getPackageManager()) != null) {
            startActivity(map);
        }
    }
}
