package com.thyago.sunshine.preferences;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.thyago.sunshine.R;
import com.thyago.sunshine.SunshineApplication;

/**
 * Created by thyago on 7/31/16.
 */
public class SunshinePrefs {
    public static String getPreferredLocation() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SunshineApplication.getSunshineContext());
        return prefs.getString(SunshineApplication.getSunshineString(R.string.pref_key_location), SunshineApplication.getSunshineString(R.string.pref_default_value_location));
    }
}
