package com.mari05lim.tandera.model.tasks;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.PersonResultsPage;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchSearchPeopleRunnable extends BasePaginatedPersonRunnable {

    private final String mQuery;

    public FetchSearchPeopleRunnable(int callingId, String query, int page) {
        super(callingId, page);
        mQuery = Preconditions.checkNotNull(query, "query cannot be null");
    }

    @Override
    public PersonResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().searchService().person(
                mQuery, //query
                getPage(), //page
                false, //include_adult
                null); //search_type
    }

    @Override
    protected MoviesState.PersonPaginatedResult getResultFromState() {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        return searchResult != null ? searchResult.people : null;
    }

    @Override
    protected void updateState(MoviesState.PersonPaginatedResult result) {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        if (searchResult != null && Objects.equal(mQuery, searchResult.query)) {
            searchResult.people = result;
            mState.setSearchResult(getCallingId(), searchResult);
        }
    }

    @Override
    protected MoviesState.PersonPaginatedResult createPaginatedResult() {
        return new MoviesState.PersonPaginatedResult();
    }

}