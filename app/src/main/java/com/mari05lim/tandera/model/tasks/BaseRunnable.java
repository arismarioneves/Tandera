package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.network.NetworkCallRunnable;
import com.mari05lim.tandera.model.network.NetworkError;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.EntityMapper;
import com.mari05lim.tandera.model.util.CountryProvider;
import com.squareup.otto.Bus;
import com.uwetrottmann.tmdb.Tmdb;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public abstract class BaseRunnable<R> extends NetworkCallRunnable<R> {

    public static final String LOG_TAG = BaseRunnable.class.getSimpleName();

    @Inject
    ApplicationState mState;

    @Inject Lazy<Tmdb> mTmdbClient;
    @Inject Lazy<Bus> mEventBus;
    @Inject Lazy<CountryProvider> mCountryProvider;
    @Inject Lazy<EntityMapper> mLazyEntityMapper;

    private final int mCallingId;

    public BaseRunnable(int callingId) {
        mCallingId = callingId;
    }

    @Override
    public void onPreExecute() {
        getEventBus().post(createLoadingProgressEvent(true));
    }


    @Override
    public void onError(RetrofitError re) {
        getEventBus().post(new BaseState.OnErrorEvent(getCallingId(),
                NetworkError.from(re)));

    }

    @Override
    public void onFinished() {
       getEventBus().post(createLoadingProgressEvent(false));
    }

    protected Bus getEventBus() {return mEventBus.get(); }

    public  Tmdb getTmdbClient() {
        return mTmdbClient.get();
    }

    public CountryProvider getCountryProvider() {
        return mCountryProvider.get();
    }

    public int getCallingId() {
        return mCallingId;
    }

    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowLoadingProgressEvent(getCallingId(), show);
    }

    public EntityMapper getEntityMapper() {
        return mLazyEntityMapper.get();
    }

}