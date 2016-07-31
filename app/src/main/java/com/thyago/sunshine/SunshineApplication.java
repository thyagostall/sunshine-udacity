package com.thyago.sunshine;

import android.app.Application;
import android.content.Context;

/**
 * Created by thyago on 7/31/16.
 */
public class SunshineApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getSunshineContext() {
        return mContext;
    }

    public static String getSunshineString(int id) {
        return mContext.getString(id);
    }
}
