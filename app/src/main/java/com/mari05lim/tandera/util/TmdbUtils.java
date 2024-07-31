package com.mari05lim.tandera.util;

import com.mari05lim.tandera.model.util.Tmdb;

/**
 * DEV Mari05liM
 */
public class TmdbUtils extends Tmdb {

    private static final String BASE_URL = "https://www.themoviedb.org/";
    private static final String PATH_MOVIES = "movie/";
    private static final String PATH_TV_SHOW = "tv/";
    private static final String PATH_PERSON = "person/";

    public static String buildMovieUrl(int movieTmdbId) {
        return BASE_URL + PATH_MOVIES + movieTmdbId;
    }

    public static String buildTvShowUrl(int showTmdbId) {
        return BASE_URL + PATH_TV_SHOW + showTmdbId;
    }

    public static String buildPersonUrl(int personTmdbId) {
        return BASE_URL + PATH_PERSON + personTmdbId;
    }

}