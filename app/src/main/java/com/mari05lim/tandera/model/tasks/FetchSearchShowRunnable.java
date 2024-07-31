package com.mari05lim.tandera.model.tasks;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.TvResultsPage;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchSearchShowRunnable extends BasePaginatedShowRunnable {
    private final String mQuery;

    public FetchSearchShowRunnable(int callingId, String query, int page) {
        super(callingId, page);
        mQuery = Preconditions.checkNotNull(query, "query cannot be null");
    }

    @Override
    public TvResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().searchService().tv(
            mQuery, //query
            getPage(), //page
            getCountryProvider().getLetterLanguageCode(), //language
            null, //first_air_date_year
            null); //search_type
    }

    @Override
    protected MoviesState.ShowPaginatedResult getResultFromState() {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        return searchResult != null ? searchResult.shows : null;
    }

    @Override
    protected void updateState(MoviesState.ShowPaginatedResult result) {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        if (searchResult != null && Objects.equal(mQuery, searchResult.query)) {
            searchResult.shows = result;
            mState.setSearchResult(getCallingId(), searchResult);
        }
    }

    @Override
    protected MoviesState.ShowPaginatedResult createPaginatedResult() {
        return new MoviesState.ShowPaginatedResult();
    }

}