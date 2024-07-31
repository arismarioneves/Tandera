package com.mari05lim.tandera.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.views.activities.SettingsActivity;

/**
 * DEV Mari05liM
 */
public class Utils {

    /**
     * Returns true if there is an active connection which is approved by the user for large data
     * downloads (e.g. images).
     *
     * @param showOfflineToast If true, displays a toast asking the user to connect to a network.
     */
    public static boolean isAllowedLargeDataConnection(Context context, boolean showOfflineToast) {
        boolean isConnected;
        boolean largeDataOverWifiOnly = TanderaPreferences.isLargeDataOverWifiOnly(context);

        // check connection state
        if (largeDataOverWifiOnly) {
            isConnected = TanderaAndroidUtils.isWifiConnected(context);
        } else {
            isConnected = TanderaAndroidUtils.isNetworkConnected(context);
        }

        // display optional offline toast
        if (showOfflineToast && !isConnected) {
            Toast.makeText(context,
                    largeDataOverWifiOnly ? R.string.offline_no_wifi : R.string.offline,
                    Toast.LENGTH_LONG).show();
        }
        return isConnected;
    }

    /**
     * Sets the global app theme variable. Applied by all activities once they are created.
     */
    public static synchronized void updateTheme(Context context, String themeIndex) {
        int theme = Integer.parseInt(themeIndex);
        switch (theme) {
            case 1: {
                SettingsActivity.THEME = R.style.Theme_Tandera_Dark;
                TanderaPreferences.setApplicationTheme(context, 1);
            }
            break;
            default: {
                SettingsActivity.THEME = R.style.Theme_Tandera_Claro;
                TanderaPreferences.setApplicationTheme(context, 0);
            }
            break;
        }
    }

    /**
     * Calls {@link Context#startActivity(Intent)} with the given {@link Intent}. If it is
     * <b>implicit</b>, makes sure there is an Activity to handle it. If <b>explicit</b>,
     * will intercept {@link android.content.ActivityNotFoundException}. Can show an error toast on
     * failure.
     * <p>
     * <p> E.g. an implicit intent may fail if e.g. the web browser has been disabled through
     * restricted profiles.
     *
     * @return Whether the {@link Intent} could be handled.
     */
    public static String getVersion(Context context) {
        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = context.getResources().getString(R.string.unknown_version);
        }
        return version;
    }

}