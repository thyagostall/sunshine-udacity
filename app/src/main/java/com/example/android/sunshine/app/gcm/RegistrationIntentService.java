package com.example.android.sunshine.app.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by thyago on 24/12/2016.
 */

public class RegistrationIntentService extends IntentService {
    private static final String LOG_TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            synchronized (LOG_TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), null);
                sendRegistrationToServer(token);

                preferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, true).apply();
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to complete token refresh");
            preferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void sendRegistrationToServer(String token) {
        Log.i(LOG_TAG, "GCM Registration token: " + token);
    }
}
