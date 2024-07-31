package com.mari05lim.tandera.util;

import android.content.Context;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.network.NetworkError;

/**
 * DEV Mari05liM
 */
public class StringUtils {

    /**
     * Decodes the {@link com.mari05lim.tandera.model.network.NetworkError} and returns integer representation. May be {@code null} if
     * error status is unknown.
     */
    public static int getMessageByError(NetworkError error) {
        switch (error) {
            case NOT_FOUND_TMDB:
                return R.string.error_movie_not_found_tmdb;
            case NETWORK_ERROR:
                return R.string.error_network;
            case UNKNOWN:
                return R.string.error_unknown;
            default:
                return R.string.error_unknown;
        }
    }

    /**
     * Decodes the movies tab title and returns integer representation.
     */
    public static int getTabTitle(UiView.MovieTabs tab) {
        switch (tab) {
            case POPULAR:
                return R.string.popular_title;
            case IN_THEATRES:
                return R.string.in_theatres_title;
//            case UPCOMING:
//                return R.string.upcoming_title;
        }
        return 0;
    }

    /**
     * Decodes the show tab title and returns integer representation.
     */
    public static int getShowsStringResId(UiView.ShowTabs tab) {
        switch (tab) {
            case POPULAR:
                return R.string.popular_title;
            case ON_THE_AIR:
                return R.string.on_the_air_title;
        }
        return 0;
    }

    /**
     * Decodes the show status and returns integer representation. May be {@code null} if
     * status is unknown.
     */
    public static int getShowStatusStringId(int encodedStatus) {
        if (encodedStatus == ShowWrapper.Status.CONTINUING) {
            return R.string.show_isalive;
        } else if (encodedStatus == ShowWrapper.Status.ENDED) {
            return R.string.show_isnotalive;
        } else {
            // status unknown, display nothing
            return R.string.show_unknown;
        }
    }

    /**
     * Builds a localized string like "Season 5" or if the number is 0 "Special Episodes".
     */
    public static String getSeasonString(Context context, int seasonNumber) {
        if (seasonNumber == 0) {
            return context.getString(R.string.tv_season_specials);
        } else {
            return context.getString(R.string.tv_season_number, seasonNumber);
        }
    }

}