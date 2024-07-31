package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.ListPeopleView;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchPeopleRunnable;
import com.mari05lim.tandera.model.util.FileLog;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class ListPeoplePresenter extends MvpPresenter<ListPeopleView> implements BaseListPresenter<ListPeopleView> {

    public ListPeoplePresenter() {
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
        FileLog.d("network results", "Search results changed");
        populateUiFromEvent(event, UiView.TanderaQueryType.SEARCH_PEOPLE);
    }

    public void search(ListPeopleView view, UiView.TanderaQueryType queryType, String query) {
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH_PEOPLE:
                getViewState().updateDisplaySubtitle(query);
                fetchPeopleSearchResults(callingId, query);
                break;
        }
    }

    @Override
    public void onUiAttached(ListPeopleView view, UiView.TanderaQueryType queryType, String parameter) {
        String title = null;
        switch (queryType) {
            case SEARCH_PEOPLE:
                title = getUiTitle(queryType);
                view.updateDisplayTitle(title);
                break;
        }

        populateUi(view, queryType);
    }

    @Override
    public String getUiTitle(UiView.TanderaQueryType queryType) {
        switch (queryType) {
            case SEARCH_PEOPLE:
                MoviesState.SearchResult result = MoviesApp.get().getState().getSearchResult();
                if (result != null) {
                    return result.query;
                } else {
                    return MoviesApp.get().getStringFetcher().getString(R.string.search_title);
                }
        }
        return null;
    }

    @Override
    public String getUiSubtitle(UiView.TanderaQueryType queryType) {
        switch (queryType) {
            case SEARCH_PEOPLE:
                return MoviesApp.get().getStringFetcher().getString(R.string.people_title);
        }
        return null;
    }

    @Override
    public void populateUi(ListPeopleView view, UiView.TanderaQueryType queryType) {

        List<PersonWrapper> items = null;
        switch (queryType) {
            case SEARCH_PEOPLE:
                MoviesState.SearchResult searchResult = MoviesApp.get().getState().getSearchResult();
                if (searchResult != null && searchResult.people != null) {
                    items = searchResult.people.items;
                    view.updateDisplaySubtitle(getUiSubtitle(UiView.TanderaQueryType.SEARCH_PEOPLE));
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
    public void refresh(ListPeopleView view, UiView.TanderaQueryType queryType) {
        //NTD
    }

    @Override
    public void onScrolledToBottom(ListPeopleView view, UiView.TanderaQueryType queryType) {
        MoviesState.SearchResult searchResult;
        switch (queryType) {
            case SEARCH_PEOPLE:
                searchResult = MoviesApp.get().getState().getSearchResult();
                if (searchResult != null && canFetchNextPage(searchResult.people)) {
                    fetchPeopleSearchResults(
                        getId(view),
                        searchResult.query,
                        searchResult.people.page + 1);
                }
                break;
        }
    }

    @Override
    public void populateUiFromEvent(BaseState.BaseEvent event, UiView.TanderaQueryType queryType) {
        final ListPeopleView ui = findUi(event.callingId);
        if (ui != null) {
            populateUi(ui, queryType);
        }
    }

    @Override
    public ListPeopleView findUi(int id) {
        for (ListPeopleView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public int getId(ListPeopleView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    private void fetchPeopleSearchResults(final int callingId, String query) {
        MoviesApp.get().getState().setSearchResult(callingId, new MoviesState.SearchResult(query));
        fetchPeopleSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchPeopleSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchPeopleRunnable(callingId, query, page));
    }

}