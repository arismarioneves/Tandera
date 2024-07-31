package com.mari05lim.tandera.model.tasks;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchSearchMovieRunnable extends BasePaginatedMovieRunnable {
    private final String mQuery;

    public FetchSearchMovieRunnable(int callingId, String query, int page) {
        super(callingId, page);
        mQuery = Preconditions.checkNotNull(query, "query cannot be null");
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().searchService().movie(
            mQuery, //query
            getPage(), //page
            getCountryProvider().getLetterLanguageCode(), //language
            false, //include_adult
            null, //year
            null, //primary_release_year
            null); //search_type
    }

    @Override
    protected MoviesState.MoviePaginatedResult getResultFromState() {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        return searchResult != null ? searchResult.movies : null;
    }

    @Override
    protected void updateState(MoviesState.MoviePaginatedResult result) {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        if (searchResult != null && Objects.equal(mQuery, searchResult.query)) {
            searchResult.movies = result;
            mState.setSearchResult(getCallingId(), searchResult);
        }
    }

    @Override
    protected MoviesState.MoviePaginatedResult createPaginatedResult() {
        return new MoviesState.MoviePaginatedResult();
    }

}