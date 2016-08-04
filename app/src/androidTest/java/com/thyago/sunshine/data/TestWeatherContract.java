package com.thyago.sunshine.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by thyago on 8/4/16.
 */
public class TestWeatherContract extends AndroidTestCase {
    private static final String TEST_WEATHER_LOCATION = "/North Pole";
    private static final long TEST_WEATHER_DATE = 1419033600L;

    public void testBuildWeatherLocation() {
        final String EXPECTED_URI = "content://com.thyago.sunshine.app/weather/%2FNorth%20Pole";
        Uri locationUri = WeatherContract.WeatherEntry.buildWeatherLocation(TEST_WEATHER_LOCATION);

        assertNotNull("Error: Null Uri returned.", locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri", locationUri.getLastPathSegment(), TEST_WEATHER_LOCATION);
        assertEquals("Error: Weather location Uri does not match our expected result.", locationUri.toString(), EXPECTED_URI);
    }
}
