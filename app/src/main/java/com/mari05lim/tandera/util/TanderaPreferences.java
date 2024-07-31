package com.mari05lim.tandera.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * DEV Mari05liM
 */
public class TanderaPreferences {

    public static final String KEY_ONLYWIFI = "com.mari05lim.tandera.updatewifionly";
    public static final String KEY_FIRST_VISIT = "com.mari05lim.tandera.visit";
    public static final String KEY_ANIMATIONS = "com.mari05lim.tandera.animations";
    public static final String KEY_THEME = "com.mari05lim.tandera.theme";
    public static final String KEY_COUNTRY = "com.mari05lim.tandera.country";
    //public static final String KEY_LANGUAGE = "com.mari05lim.tandera.language";

    public static boolean isLargeDataOverWifiOnly(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_ONLYWIFI, false);
    }

    public static void setLargeDataOverWifiOnly(Context mContext, boolean allow) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(KEY_ONLYWIFI, allow).apply();
    }

    public static boolean areAnimationsEnabled(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_ANIMATIONS, true);
    }

    public static void setAnimationsEnabled(Context mContext, boolean enabled) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(KEY_ANIMATIONS, enabled).apply();
    }

    public static int getApplicationTheme(Context mContext) {
        return  Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).getString(KEY_THEME, "0"));
    }

    public static void setApplicationTheme(Context mContext, int themeNumber) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(KEY_THEME, String.valueOf(themeNumber)).apply();
    }

    public static int getApplicationContry(Context mContext) {
        return  Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).getString(KEY_COUNTRY, "0"));
    }

    public static void setApplicationCountry(Context mContext, int countryNumber) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(KEY_COUNTRY, String.valueOf(countryNumber)).apply();
    }

    /*public static int getApplicationLanguage(Context mContext) {
        return  Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).getString(KEY_LANGUAGE, "0"));
    }

    public static void setApplicationLanguage(Context mContext, int languageNumber) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(KEY_LANGUAGE, String.valueOf(languageNumber)).apply();
    }*/

    public static void setFirstVisitPerformed(Context mContext, boolean performed) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(KEY_FIRST_VISIT, performed).apply();
    }

    public static boolean isSetFirstVisitAsPerformed(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).contains(KEY_FIRST_VISIT);
    }

}