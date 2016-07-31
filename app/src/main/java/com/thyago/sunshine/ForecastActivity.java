package com.thyago.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

public class ForecastActivity extends AppCompatActivity {

    private static final String LOG_TAG = ForecastActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
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
