package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.ListTvShowsView;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.FetchOnTheAirShowsRunnable;
import com.mari05lim.tandera.model.tasks.FetchPopularShowsRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchShowRunnable;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class ListTvShowsPresenter extends MvpPresenter<ListTvShowsView> implements BaseListPresenter<ListTvShowsView> {

    public ListTvShowsPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    @Subscribe
    public void onSearchResultChanged(MoviesState.SearchResultChangedEvent event) {
        populateUiFromEvent(event, UiView.TanderaQueryType.SEARCH_PEOPLE);
    }

    @Subscribe
    public void onPopularChanged(MoviesState.PopularShowsChangedEvent event) {
        populateUiFromEvent(event, UiView.TanderaQueryType.POPULAR_SHOWS);
    }

    @Subscribe
    public void onTheAirChanged(MoviesState.OnTheAirShowsChangedEvent event) {
        populateUiFromEvent(event, UiView.TanderaQueryType.ON_THE_AIR_SHOWS);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        ListTvShowsView ui = findUi(event.callingId);
        if (ui != null) {
            ui.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        ListTvShowsView ui = findUi(event.callingId);
        if (ui != null) {
            if (!event.secondary) {
                getViewState().showLoadingProgress(event.show);
            }
        }
    }

    public void search(ListTvShowsView view, UiView.TanderaQueryType queryType, String query) {
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH_SHOWS:
                fetchTvShowsSearchResults(callingId, query);
                break;
        }
    }

    @Override
    public void onUiAttached(ListTvShowsView view, UiView.TanderaQueryType queryType, String parameter) {
        String title = null;
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH_SHOWS:
                title = getUiTitle(queryType);
                view.updateDisplayTitle(title);
                break;
            case POPULAR_SHOWS:
                fetchPopularIfNeeded(callingId);
                break;
            case ON_THE_AIR_SHOWS:
                fetchOnTheAirIfNeeded(callingId);
                break;
        }
        populateUi(view, queryType);
    }

    @Override
    public String getUiTitle(UiView.TanderaQueryType queryType) {
        switch (queryType) {
            case SEARCH_SHOWS: {
                MoviesState.SearchResult result = MoviesApp.get().getState().getSearchResult();
                if (result != null) {
                    return result.query;
                } else {
                    return MoviesApp.get().getStringFetcher().getString(R.string.search_title);
                }
            }
            case POPULAR_SHOWS:
                return MoviesApp.get().getStringFetcher().getString(R.string.popular_title);
            case ON_THE_AIR_SHOWS:
                return MoviesApp.get().getStringFetcher().getString(R.string.on_the_air_title);
        }
        return null;
    }

    @Override
    public String getUiSubtitle(UiView.TanderaQueryType queryType) {
        switch (queryType) {
            case SEARCH_SHOWS:
                return MoviesApp.get().getStringFetcher().getString(R.string.shows_title);
        }
        return null;
    }

    @Override
    public void populateUi(ListTvShowsView view, UiView.TanderaQueryType queryType) {

        List<ShowWrapper> items = null;
        switch (queryType) {
            case SEARCH_SHOWS:
                MoviesState.SearchResult searchResult = MoviesApp.get().getState().getSearchResult();
                if (searchResult != null && searchResult.shows != null) {
                    items = searchResult.shows.items;
                    view.updateDisplaySubtitle(getUiSubtitle(UiView.TanderaQueryType.SEARCH_SHOWS));
                }
                break;
            case POPULAR_SHOWS:
                ApplicationState.ShowPaginatedResult popular = MoviesApp.get().getState().getPopularShows();
                if (popular != null) {
                    items = popular.items;
                }
                break;
            case ON_THE_AIR_SHOWS:
                ApplicationState.ShowPaginatedResult onTheAir = MoviesApp.get().getState().getOnTheAirShows();
                if (onTheAir != null) {
                    items = onTheAir.items;
                }
                break;
        }
        view.setData(items);
    }

    @Override
    public boolean canFetchNextPage(BaseState.PaginatedResult<?> paginatedResult) {
        return paginatedResult != null && paginatedResult.page < paginatedResult.totalPages;
    }

    @Override
    public void refresh(ListTvShowsView view, UiView.TanderaQueryType queryType) {
        final int callingId = getId(view);

        switch (queryType) {
            case POPULAR_SHOWS:
                fetchPopular(callingId);
                break;
            case ON_THE_AIR_SHOWS:
                fetchOnTheAir(callingId);
                break;
        }
    }

    @Override
    public void onScrolledToBottom(ListTvShowsView view, UiView.TanderaQueryType queryType) {
        MoviesState.SearchResult searchResult;
        ApplicationState.ShowPaginatedResult result;
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH_SHOWS:
                searchResult = MoviesApp.get().getState().getSearchResult();
                if (searchResult != null && canFetchNextPage(searchResult.shows)) {
                    fetchTvShowsSearchResults(
                            getId(view),
                            searchResult.query,
                            searchResult.people.page + 1);
                }
                break;

            case POPULAR_SHOWS:
                result = MoviesApp.get().getState().getPopularShows();
                if (canFetchNextPage(result)) {
                    fetchPopular(callingId, result.page + 1);
                }
                break;

            case ON_THE_AIR_SHOWS:
                result = MoviesApp.get().getState().getOnTheAirShows();
                if (canFetchNextPage(result)) {
                    fetchOnTheAir(callingId, result.page + 1);
                }
                break;
        }
    }

    @Override
    public void populateUiFromEvent(BaseState.BaseEvent event, UiView.TanderaQueryType queryType) {
        final ListTvShowsView ui = findUi(event.callingId);
        if (ui != null) {
            populateUi(ui, queryType);
        }
    }

    @Override
    public ListTvShowsView findUi(int id) {
        for (ListTvShowsView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public int getId(ListTvShowsView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    /**
     * Fetch search task
     */
    private void fetchTvShowsSearchResults(final int callingId, String query) {
        MoviesApp.get().getState().setSearchResult(callingId, new MoviesState.SearchResult(query));
        fetchTvShowsSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchTvShowsSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchShowRunnable(callingId, query, page));
    }

    /**
     * Fetch popular shows task
     */
    private void fetchPopular(final int callingId, final int page) {
        executeNetworkTask(new FetchPopularShowsRunnable(callingId, page));
    }

    private void fetchPopular(final int callingId) {
        MoviesApp.get().getState().setPopularShows(callingId, null);
        fetchPopular(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchPopularIfNeeded(final int callingId) {
        ApplicationState.ShowPaginatedResult popular = MoviesApp.get().getState().getPopularShows();
        if (popular == null || MoviesCollections.isEmpty(popular.items)) {
            fetchPopular(callingId, TMDB_FIRST_PAGE);
        } else {
            final ListTvShowsView ui = findUi(callingId);
            if (ui != null) {
                populateUi(ui, UiView.TanderaQueryType.POPULAR_SHOWS);
            }
        }
    }

    /**
     * Fetch on the air shows task
     */
    private void fetchOnTheAir(final int callingId, final int page) {
        executeNetworkTask(new FetchOnTheAirShowsRunnable(callingId, page));
    }

    private void fetchOnTheAir(final int callingId) {
        MoviesApp.get().getState().setOnTheAirShows(callingId, null);
        fetchOnTheAir(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchOnTheAirIfNeeded(final int callingId) {
        ApplicationState.ShowPaginatedResult onTheAir = MoviesApp.get().getState().getOnTheAirShows();
        if (onTheAir == null || MoviesCollections.isEmpty(onTheAir.items)) {
            fetchOnTheAir(callingId, TMDB_FIRST_PAGE);
        } else {
            final ListTvShowsView ui = findUi(callingId);
            if (ui != null) {
                populateUi(ui, UiView.TanderaQueryType.ON_THE_AIR_SHOWS);
            }
        }
    }

}