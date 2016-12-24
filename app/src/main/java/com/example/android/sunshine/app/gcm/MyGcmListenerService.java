package com.example.android.sunshine.app.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thyago on 24/12/2016.
 */

public class MyGcmListenerService extends GcmListenerService {
    private static final String LOG_TAG = MyGcmListenerService.class.getSimpleName();

    private static final String EXTRA_DATA = "data";
    private static final String EXTRA_WEATHER = "weather";
    private static final String EXTRA_LOCATION = "location";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data.isEmpty()) {
            return;
        }

        String senderId = getString(R.string.gcm_defaultSenderId);
        if (senderId.length() == 0) {
            Log.e(LOG_TAG, "SenderID string needs to be set.");
            return;
        }

        if (!senderId.equals(from)) {
            return;
        }

        try {
            JSONObject jObject = new JSONObject(data.getString(EXTRA_DATA));
            String weather = jObject.getString(EXTRA_WEATHER);
            String location = jObject.getString(EXTRA_LOCATION);
            String alert = String.format(getString(R.string.gcm_weather_alert), weather, location);
            sendNotificationMessage(alert);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotificationMessage(String message) {
        final int REQUEST_CODE = 0;
        final int NO_FLAGS = 0;
        final int NOTIFICATION_ID = 1;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE, new Intent(this, MainActivity.class), NO_FLAGS);

        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.art_storm);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.art_clear)
                .setLargeIcon(largeIcon)
                .setContentTitle(getString(R.string.gcm_weather_alert_title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .build();
        manager.notify(NOTIFICATION_ID, notification);
    }
}
