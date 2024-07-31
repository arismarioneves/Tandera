package com.mari05lim.tandera.model;

/**
 * DEV Mari05liM
 */
public class Constants {

    public static final String TMDB_API_KEY = "";
    public static final boolean DEBUG_NETWORK = false;

    public static final String SUPPORT_MAIL = "";

    public final static String DB_NAME = "tandera.db";
    public static final int DATABASE_VERSION = 1;

    public final static String API_VERSION = "app/"; // Sempre colocar uma ( / barra) no final
    public final static String APP_VERSION = "2.3";

    //Ads Configuration
    public static final boolean ENABLE_ADMOB_BANNER_ADS = false;
    public static final boolean ENABLE_ADMOB_INTERSTITIAL_ADS = false;
    public static final int ADMOB_INTERSTITIAL_ADS_INTERVAL = 3;

    //ISO 3166-1 default
    public final static String TWO_LETTER_CODE = "BR";

    public final static String mCountryCode_PtBR = "BRA";
    public final static String mCountryCode_EnUS = "USA";
    //https://pt.wikipedia.org/wiki/ISO_3166-1

    public final static String mLanguageCode_PtBR = "pt-BR";
    public final static String mLanguageCode_EnUS = "en-US";
    //http://www.lingoes.net/en/translator/langcode.htm

    //Site Tandera
    public final static String SITE_TANDERA = ""; // Sempre colocar uma ( / barra) no final

    //API Tandera
    public final static String API_TANDERA = ""; // Sempre colocar uma ( / barra) no final

    public static final String ROBOTO_CONDENSED = "RobotoCondensed-Regular.ttf";
    public static final String ROBOTO_CONDENSED_BOLD = "RobotoCondensed-Bold.ttf";
    public static final String ROBOTO_CONDENSED_LIGHT = "RobotoCondensed-Light.ttf";

    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    public static final long STALE_MOVIE_DETAIL_THRESHOLD = 2 * DAY_IN_MILLIS;
    public static final long FULL_MOVIE_DETAIL_ATTEMPT_THRESHOLD = 60 * 60 * 1000; // 60 secs
}