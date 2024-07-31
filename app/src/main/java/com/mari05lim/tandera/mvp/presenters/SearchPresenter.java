package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.SearchView;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchMovieRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchPeopleRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchShowRunnable;
import com.squareup.otto.Subscribe;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class SearchPresenter extends MvpPresenter<SearchView> implements BasePresenter<SearchView> {

    private static final String LOG_TAG = SearchPresenter.class.getSimpleName();

    public SearchPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    public void search(SearchView view, UiView.TanderaQueryType queryType, String query) {
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH:
                fetchSearchResults(callingId, query);
                break;
        }
    }

    public void clearSearch(SearchView view) {
        final int callingId = getId(view);
        MoviesApp.get().getState().setSearchResult(callingId, null);
    }

    public String getUiTitle(UiView.TanderaQueryType queryType) {
        switch (queryType) {
            case SEARCH:
                MoviesState.SearchResult result = MoviesApp.get().getState().getSearchResult();
                if (result != null) {
                    return result.query;
                } else {
                    return MoviesApp.get().getStringFetcher().getString(R.string.search_title);
                }
        }
        return null;
    }

    public void onUiAttached(SearchView view, UiView.TanderaQueryType queryType, String parameter) {
        populateUi(view, queryType);
    }

    public void populateUi(SearchView view, UiView.TanderaQueryType queryType) {
        view.setData(MoviesApp.get().getState().getSearchResult());
    }

    public void refresh(SearchView view, UiView.TanderaQueryType queryType) {
        //NTD
    }

    public void onScrolledToBottom(SearchView view, UiView.TanderaQueryType queryType) {
        //NTD
    }

    @Override
    public int getId(SearchView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Subscribe
    public void onSearchResultChanged(MoviesState.SearchResultChangedEvent event) {
        populateUi(getViewState(), UiView.TanderaQueryType.SEARCH);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (null != event.error) {
            getViewState().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
            if (!event.secondary) {
                getViewState().showLoadingProgress(event.show);
            }
    }

    private void fetchSearchResults(final int callingId, String query) {
        MoviesApp.get().getState().setSearchResult(callingId, new MoviesState.SearchResult(query));
        fetchMovieSearchResults(callingId, query, TMDB_FIRST_PAGE);
        fetchPeopleSearchResults(callingId, query, TMDB_FIRST_PAGE);
        fetchShowsSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchMovieSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchMovieRunnable(callingId, query, page));
    }

    private void fetchPeopleSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchPeopleRunnable(callingId, query, page));
    }

    private void fetchShowsSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchShowRunnable(callingId, query, page));
    }

}