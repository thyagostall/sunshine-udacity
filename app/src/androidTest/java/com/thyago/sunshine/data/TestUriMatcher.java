package com.thyago.sunshine.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by thyago on 8/4/16.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final String LOCATION_QUERY = "London, UK";
    private static final long TEST_DATE = 1419033600L;
    private static final long TEST_LOCATION_ID = 10L;

    private static final Uri TEST_WEATHER_DIR = WeatherContract.WeatherEntry.CONTENT_URI;
    private static final Uri TEST_WEATHER_WITH_LOCATION_DIR = WeatherContract.WeatherEntry.buildWeatherLocation(LOCATION_QUERY);
    private static final Uri TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(LOCATION_QUERY, TEST_DATE);

    private static final Uri TEST_LOCATION_DIR = WeatherContract.LocationEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = WeatherProvider.buildUriMatcher();

        assertEquals("Error: The WEATHER URI was matched incorrectly.", WeatherProvider.WEATHER, testMatcher.match(TEST_WEATHER_DIR));
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.", WeatherProvider.WEATHER_WITH_LOCATION, testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR));
        assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.", WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE, testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR));
        assertEquals("Error: The LOCATION URI was matched incorrectly.", WeatherProvider.LOCATION, testMatcher.match(TEST_LOCATION_DIR));
    }
}
