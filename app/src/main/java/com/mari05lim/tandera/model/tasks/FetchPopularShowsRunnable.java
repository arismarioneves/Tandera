package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.TvResultsPage;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchPopularShowsRunnable extends BasePaginatedShowRunnable {

    public FetchPopularShowsRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    public TvResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvService().popular(getPage(),
                getCountryProvider().getLetterLanguageCode());
    }

    @Override
    protected ApplicationState.ShowPaginatedResult getResultFromState() {
        return mState.getPopularShows();
    }

    @Override
    protected void updateState(ApplicationState.ShowPaginatedResult result) {
        mState.setPopularShows(getCallingId(), result);
    }

}