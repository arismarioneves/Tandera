package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.TvResultsPage;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchOnTheAirShowsRunnable extends BasePaginatedShowRunnable {

    public FetchOnTheAirShowsRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    public TvResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvService().onTheAir(getPage(),
                getCountryProvider().getLetterLanguageCode());
    }

    @Override
    protected void updateState(ApplicationState.ShowPaginatedResult result) {
      mState.setOnTheAirShows(getCallingId(), result);
    }

    @Override
    protected ApplicationState.ShowPaginatedResult getResultFromState() {
        return mState.getOnTheAirShows();
    }

}