package com.mari05lim.tandera.model.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.sqlite.Column;
import com.mari05lim.tandera.model.sqlite.SQLiteHelper;

/**
 * DEV Mari05liM
 */
public class MovieRepository extends SqLiteRepository<MovieWrapper> {

    public MovieRepository(SQLiteHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected String getTableName() {
        return "Movie";
    }

    @Override
    protected Column[] getColumns() {
        return MovieColumns.values();
    }

    @Override
    protected MovieWrapper createObjectFromCursor(Cursor cursor) {
        String dbId = MovieColumns.ID.readValue(cursor);
        MovieWrapper movie = new MovieWrapper(dbId);
        movie.setTitle(MovieColumns.TITLE.readValue(cursor));
        movie.setTmdbBackdropUrl(MovieColumns.BACKDROP_PATH.readValue(cursor));
        movie.setTmdbPosterUrl(MovieColumns.POSTER_PATH.readValue(cursor));
        movie.setOverview(MovieColumns.OVERVIEW.readValue(cursor));
        movie.setReleasedTime(MovieColumns.RELEASE_DATE.readValue(cursor));
        movie.setTmdbRatingVotesAmount(MovieColumns.VOTE_COUNT.readValue(cursor));
        movie.setTmdbRatingVotesAverage(MovieColumns.VOTE_AVERAGE.readValue(cursor));
        movie.setWatched(MovieColumns.IS_WATCHED.readValue(cursor));
        return movie;
    }

    @Override
    protected ContentValues createContentValuesFromObject(MovieWrapper item) {
        ContentValues movieValue = new ContentValues();
        MovieColumns.ID.addValue(movieValue, String.valueOf(item.getTmdbId()));
        MovieColumns.TITLE.addValue(movieValue, item.getTitle());
        MovieColumns.BACKDROP_PATH.addValue(movieValue, item.getBackdropUrl());
        MovieColumns.POSTER_PATH.addValue(movieValue, item.getPosterUrl());
        MovieColumns.OVERVIEW.addValue(movieValue, item.getOverview());
        MovieColumns.RELEASE_DATE.addValue(movieValue, item.getReleasedTime());
        MovieColumns.VOTE_AVERAGE.addValue(movieValue, item.getVotesAverage());
        MovieColumns.VOTE_COUNT.addValue(movieValue, item.getRatingVotes());
        MovieColumns.IS_WATCHED.addValue(movieValue, item.isWatched());
        return movieValue;
    }

}