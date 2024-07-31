package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DEV Mari05liM
 */
public class FetchWatchedRunnable extends DatabaseBackgroundRunnable<List<Watchable>> {

    private final ApplicationState.Callback<List<Watchable>> callback;

    public FetchWatchedRunnable(int callingId, final ApplicationState.Callback<List<Watchable>> callback) {
        super(callingId);
        this.callback = callback;
    }

    @Override
    public List<Watchable> doDatabaseCall() {

        ArrayList watchedList = Lists.newArrayList();
        List<MovieWrapper> movies = mState.getRepositoryInstance(MovieWrapper.class).getAll();

        for (MovieWrapper movie: movies) {
            if (movie.isWatched()) {
                watchedList.add(movie);
            }
        }

        List<ShowWrapper> shows = mState.getRepositoryInstance(ShowWrapper.class).getAll();

        for (ShowWrapper show: shows) {
            if (show.isWatched()) {
                watchedList.add(show);
            }
        }

        if (!watchedList.isEmpty()) {
            Collections.sort(watchedList, Watchable.COMPARATOR__ITEM_DATE_DESC);
        }
        return watchedList;
    }

    @Override
    public void postExecute(List<Watchable> result) {
        if (result != null) {
            callback.onFinished(result);
        }
    }

}