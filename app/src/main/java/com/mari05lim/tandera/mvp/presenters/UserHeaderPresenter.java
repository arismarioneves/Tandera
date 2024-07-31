package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.views.UserHeaderView;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.DatabaseBackgroundRunnable;
import com.mari05lim.tandera.model.util.FileLog;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class UserHeaderPresenter extends MvpPresenter<UserHeaderView> implements BasePresenter<UserHeaderView> {

    public UserHeaderPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    public void onUiAttached(UserHeaderView view) {
        final int callingId = getId(view);
        fetchWatchedIfNeeded(callingId);
        populateUi(view);
    }

    @Subscribe
    public void onWatchedChanged(MoviesState.WatchedChangedEvent event) {
        populateUi(getViewState());
    }

    public void populateUi(UserHeaderView view) {
        int[] watchedCount = new int[]{0, 0};
        List<Watchable> items = MoviesApp.get().getState().getWatched();

        for (Watchable item: items) {
            switch (item.getWatchableType()) {
                case MOVIE:
                    ++watchedCount[0];
                    break;
                case TV_SHOW:
                    ++watchedCount[1];
            }
        }
        view.setData(watchedCount);
    }

    @Override
    public int getId(UserHeaderView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
    }

    private void fetchWatchedIfNeeded(final int callingId) {
        FileLog.d("watched", "Is populated from DB = " + MoviesApp.get().getState().isPopulatedWatchedFromDb());
        if (!MoviesApp.get().getState().isPopulatedWatchedFromDb() || MoviesCollections.isEmpty(MoviesApp.get().getState().getWatched())) {
            fetchWatched(callingId);
        }
    }

    private void fetchWatched(final int callingId) {
        FileLog.d("watched", "ListWatchedPresenter: Fetching watched from db");
        if (MoviesApp.get().isAuthentificatedFeatureEnabled()) {
           // executeBackgroundTask(new FetchWatchedRunnable(callingId, new ApplicationState.WatchedDbLoadCallback()));
        }
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

}