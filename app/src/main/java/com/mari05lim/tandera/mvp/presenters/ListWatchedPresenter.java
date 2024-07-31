package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.callbacks.WatchedDbLoadCallback;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.mvp.views.WatchedListView;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.DatabaseBackgroundRunnable;
import com.mari05lim.tandera.model.tasks.FetchWatchedRunnable;
import com.mari05lim.tandera.model.util.FileLog;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class ListWatchedPresenter extends MvpPresenter<WatchedListView> implements BaseListPresenter<WatchedListView> {

    public ListWatchedPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    @Override
    public void onUiAttached(WatchedListView view, UiView.TanderaQueryType queryType, String parameter) {
        final int callingId = getId(view);
        switch (queryType) {
            case WATCHED:
                fetchWatchedIfNeeded(callingId);
                break;
        }
        view.updateDisplayTitle(getUiTitle(queryType));
        populateUi(view, queryType);
    }

    @Override
    public String getUiTitle(UiView.TanderaQueryType queryType) {
        switch (queryType) {
            case WATCHED:
                return MoviesApp.get().getStringFetcher().getString(R.string.watched_title);
        }
        return null;
    }

    @Override
    public String getUiSubtitle(UiView.TanderaQueryType queryType) {
        return null;
    }

    @Override
    public void populateUi(WatchedListView view, UiView.TanderaQueryType queryType) {
        FileLog.d("watched", "Populate UI by query = " + queryType);
        List<Watchable> items = null;
        switch (queryType) {
            case WATCHED:
                items = MoviesApp.get().getState().getWatched();
                Collections.sort(items, Watchable.COMPARATOR__ITEM_DATE_DESC);
                break;
        }

        view.updateDisplayTitle(getUiTitle(UiView.TanderaQueryType.WATCHED));
        assert items != null;
        if (items.size() == 0) {
            view.showError(null);
        } else {
            view.setData(items);
        }
    }

    @Override
    public boolean canFetchNextPage(BaseState.PaginatedResult<?> paginatedResult) {
        return false;
    }

    @Override
    public void refresh(WatchedListView view, UiView.TanderaQueryType queryType) {
        //NTD
    }

    @Override
    public void onScrolledToBottom(WatchedListView view, UiView.TanderaQueryType queryType) {
        //NTD
    }

    @Override
    public void populateUiFromEvent(BaseState.BaseEvent event, UiView.TanderaQueryType queryType) {
        //NTD
    }

    @Override
    public WatchedListView findUi(int id) {
        for (WatchedListView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public int getId(WatchedListView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Subscribe
    public void onWatchedChanged(MoviesState.WatchedChangedEvent event) {
        populateUi(getViewState(), UiView.TanderaQueryType.WATCHED);
    }

    private void fetchWatchedIfNeeded(final int callingId) {
          if (!MoviesApp.get().getState().isPopulatedWatchedFromDb() || MoviesCollections.isEmpty(MoviesApp.get().getState().getWatched())) {
            fetchWatched(callingId);
        }
    }

    private void fetchWatched(final int callingId) {
        if (MoviesApp.get().isAuthentificatedFeatureEnabled()) {
            executeBackgroundTask(new FetchWatchedRunnable(callingId, new WatchedDbLoadCallback()));
        }
    }

}