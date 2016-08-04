package com.thyago.sunshine.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;

/**
 * Created by thyago on 8/4/16.
 */
public class TestProvider extends AndroidTestCase {

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), WeatherProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: Weather provider not registered correctly", providerInfo.authority, WeatherContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(), false);
        }
    }

}
