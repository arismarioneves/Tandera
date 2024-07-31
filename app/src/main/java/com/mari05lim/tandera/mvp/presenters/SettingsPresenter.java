package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.views.SettingsView;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.ClearWatchedRunnable;
import com.mari05lim.tandera.model.tasks.DatabaseBackgroundRunnable;
import com.mari05lim.tandera.model.util.FileLog;
import com.squareup.otto.Subscribe;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class SettingsPresenter extends MvpPresenter<SettingsView> {

    public SettingsPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    @Subscribe
    public void onWatchedChanged(MoviesState.WatchedClearedEvent event) {
        FileLog.d("watched", "MainPresenter: on watched changed");

        getViewState().onWatchedCleared();
    }

    private  <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    public int getId(SettingsView view) {
        return view.hashCode();
    }

    public void clearWatched(SettingsView view) {
        final int callingId = getId(view);
        if (MoviesApp.get().isAuthentificatedFeatureEnabled()) {
            executeBackgroundTask(new ClearWatchedRunnable(callingId, new WatchedDbClearedCallback()));
        }
    }

    public final class WatchedDbClearedCallback implements ApplicationState.Callback<Void> {
        @Override
        public void onFinished(Void result) {
            MoviesApp.get().getState().setWatchedCleared();
            MoviesApp.get().getState().setPopulatedWatchedFromDb(true);
        }
    }

}