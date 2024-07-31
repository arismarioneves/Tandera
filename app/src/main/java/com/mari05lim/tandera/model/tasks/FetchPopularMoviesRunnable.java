package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchPopularMoviesRunnable extends BasePaginatedMovieRunnable {

    public FetchPopularMoviesRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().popular(getPage(),
                getCountryProvider().getLetterLanguageCode());

    }

    @Override
    protected ApplicationState.MoviePaginatedResult getResultFromState() {
        return mState.getPopularMovies();
    }

    @Override
    protected void updateState(ApplicationState.MoviePaginatedResult result) {
        mState.setPopularMovies(getCallingId(), result);
    }

}