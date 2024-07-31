package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchInTheatresRunnable extends BasePaginatedMovieRunnable {

    public FetchInTheatresRunnable(int callingId, int page) {
        super(callingId, page);
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().nowPlaying(
                getPage(),
                getCountryProvider().getLetterLanguageCode());
    }

    @Override
    protected ApplicationState.MoviePaginatedResult getResultFromState() {
        return mState.getNowPlaying();
    }

    @Override
    protected void updateState(ApplicationState.MoviePaginatedResult result) {
        mState.setNowPlaying(getCallingId(), result);
    }
}
