package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import java.util.ArrayList;
import java.util.List;

/**
 * DEV Mari05liM
 */
abstract class BasePaginatedMovieRunnable extends BasePaginatedRunnable <
        ApplicationState.MoviePaginatedResult, MovieWrapper, MovieResultsPage> {

    public BasePaginatedMovieRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    protected void updatePaginatedResult(
            ApplicationState.MoviePaginatedResult result,
            MovieResultsPage tmdbResult) {
        List<MovieWrapper> movies = new ArrayList<>(tmdbResult.results.size());
        for (Movie mMovie: tmdbResult.results) {
            movies.add(getEntityMapper().map(mMovie));
        }

        result.items.addAll(movies);

        result.page = tmdbResult.page;
        if (tmdbResult.total_pages != null) {
            result.totalPages = tmdbResult.total_pages;
        }
    }

    @Override
    protected ApplicationState.MoviePaginatedResult createPaginatedResult() {
        return new ApplicationState.MoviePaginatedResult();
    }

}