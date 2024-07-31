package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.entities.WatchableType;
import com.mari05lim.tandera.model.repository.Repository;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.util.FileLog;

import java.util.Collections;
import java.util.List;

/**
 * DEV Mari05liM
 */
public abstract class WatchableActionRunnable<M extends Watchable> extends DatabaseBackgroundRunnable<Void> {

    private final M  mItem;

    public WatchableActionRunnable(int callingId, M item) {
        super(callingId);
        this.mItem = item;
        itemRequiresModifying(mItem);
    }

    protected abstract void itemRequiresModifying(M item);

    @Override
    public void preExecute() {
        checkState(mItem);
    }

    @Override
    public Void doDatabaseCall() {
        Repository movieRepository = mState.getRepositoryInstance(MovieWrapper.class);
        Repository showRepository = mState.getRepositoryInstance(ShowWrapper.class);

        //boolean shouldMarkAsWatched = mItem.isWatched();
        //if (shouldMarkAsWatched) {
            if ((WatchableType.MOVIE).equals(mItem.getWatchableType())) {
                MovieWrapper movie = (MovieWrapper)movieRepository.get(String.valueOf(mItem.getTmdbId()));
                if (movie != null) {
                    FileLog.d("sqlite", "Update movie");
                    movieRepository.update(mItem);
                } else {
                    FileLog.d("sqlite", "Add movie");
                    movieRepository.add(mItem);
                }
            } else if ((WatchableType.TV_SHOW).equals(mItem.getWatchableType())) {
                ShowWrapper show = (ShowWrapper) showRepository.get(String.valueOf(mItem.getTmdbId()));
                if (show != null) {
                    showRepository.update(mItem);
                } else {
                    showRepository.add(mItem);
                }
            }
        //}
        return null;
    }

    protected void checkState(M item) {
        final List<Watchable> watched = mState.getWatched();

        //if (!MoviesCollections.isEmpty(watched)) {
        boolean shouldMarkAsWatched = item.isWatched();

        if (shouldMarkAsWatched != watched.contains(item)) {
            if (shouldMarkAsWatched) {
                watched.add(item);
                Collections.sort(watched, Watchable.COMPARATOR__ITEM_DATE_DESC);
            } else {
                watched.remove(item);
            }
        }
        //}
    }

    protected M getItem() {
        return mItem;
    }

    @Override
    public void postExecute(Void result) {
        getEventBus().post(new MoviesState.WatchedChangedEvent());
    }

}