package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.network.BackgroundCallRunnable;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * DEV Mari05liM
 */
public abstract class DatabaseBackgroundRunnable<R> extends BackgroundCallRunnable<R> {

    @Inject ApplicationState mState;
    @Inject Lazy<Bus> mEventBus;

    private final int mCallingId;

    public DatabaseBackgroundRunnable(int callingId) {
        mCallingId = callingId;
    }

    @Override
    public final R runAsync() {


        return doDatabaseCall();
    }

    public abstract R doDatabaseCall();

    protected Bus getEventBus() {return mEventBus.get(); }

    public int getCallingId() {
        return mCallingId;
    }
}