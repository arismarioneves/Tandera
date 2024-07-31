package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.state.ApplicationState;

/**
 * DEV Mari05liM
 */
public class ClearWatchedRunnable extends DatabaseBackgroundRunnable<Void>{

    private final ApplicationState.Callback<Void> callback;

    public ClearWatchedRunnable(int callingId,  final ApplicationState.Callback<Void> callback) {
        super(callingId);
        this.callback = callback;
    }

    @Override
    public Void doDatabaseCall() {
        mState.getRepositoryInstance(MovieWrapper.class).removeAll();
        mState.getRepositoryInstance(ShowWrapper.class).removeAll();
        return null;
    }

    @Override
    public void postExecute(Void result) {
        callback.onFinished(result);
    }

}