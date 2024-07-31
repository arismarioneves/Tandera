package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.views.TvShowWatchedView;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.DatabaseBackgroundRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntitySeenRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntityUnseenRunnable;
import com.squareup.otto.Subscribe;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class TvShowPresenter extends MvpPresenter<TvShowWatchedView> {

    public static final String TAG = "TvShowPresenter";

    public TvShowPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    public int getId(TvShowWatchedView view) {
        return view.hashCode();
    }

    public void toggleShowWatched(ShowWrapper item, int position) {
        Preconditions.checkNotNull(item, "show cannot be null");
        if (item.isWatched()) {
            markShowUnseen(position, item);
        } else {
            markShowSeen(position, item);
        }
    }

    private void markShowSeen(int callingId, ShowWrapper item) {
        executeBackgroundTask(new MarkEntitySeenRunnable(callingId, item));
    }

    private void markShowUnseen(int callingId, ShowWrapper item) {
        executeBackgroundTask(new MarkEntityUnseenRunnable(callingId, item));
    }

}