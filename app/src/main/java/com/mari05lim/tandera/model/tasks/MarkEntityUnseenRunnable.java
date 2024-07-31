package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.state.MoviesState;

/**
 * DEV Mari05liM
 */
public class MarkEntityUnseenRunnable<W extends Watchable> extends WatchableActionRunnable<W> {

    public MarkEntityUnseenRunnable(int callingId, W item) {
        super(callingId, item);
    }

    @Override
    protected void itemRequiresModifying(W item) {
        item.setWatched(false);
    }

    @Override
    public void postExecute(Void result) {
        super.postExecute(result);
        switch (getItem().getWatchableType()) {
            case MOVIE:
                getEventBus().post(new MoviesState.MovieFlagUpdateEvent(getCallingId(), (MovieWrapper)getItem()));
                break;
            case TV_SHOW:
                getEventBus().post(new MoviesState.ShowFlagUpdateEvent(getCallingId(), (ShowWrapper)getItem()));
                break;
        }
    }

}