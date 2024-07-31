package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.callbacks.WatchedDbLoadCallback;
import com.mari05lim.tandera.mvp.views.MainView;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.DatabaseBackgroundRunnable;
import com.mari05lim.tandera.model.tasks.FetchWatchedRunnable;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public MainPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    @Subscribe
    public void onWatchedMoviesChanged(MoviesState.WatchedChangedEvent event) {
        populateUi(getViewState());
    }

    public int getId(MainView view) {
        return view.hashCode();
    }

    public void populateUi(MainView view) {
        int[] watchedCount = new int[]{0, 0};
        List<Watchable> items = MoviesApp.get().getState().getWatched();

        for (Watchable item: items) {
            switch (item.getWatchableType()) {
                case MOVIE:
                    watchedCount[0]++;
                    break;
                case TV_SHOW:
                    watchedCount[1]++;
                    break;
            }
        }
        view.setData(watchedCount);
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        final int callingId = getId(view);
            populateStateFromDb(callingId);
            populateUi(view);
    }

    public void populateStateFromDb(int callingId) {
        if (!MoviesApp.get().getState().isPopulatedWatchedFromDb() || MoviesCollections.isEmpty(MoviesApp.get().getState().getWatched())) {
            fetchWatched(callingId);
        }
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    public void fetchWatched(final int callingId) {
        if (MoviesApp.get().isAuthentificatedFeatureEnabled()) {
            executeBackgroundTask(new FetchWatchedRunnable(callingId, new WatchedDbLoadCallback()));
        }
    }

}