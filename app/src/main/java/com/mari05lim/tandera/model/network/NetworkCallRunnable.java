package com.mari05lim.tandera.model.network;

import retrofit.RetrofitError;

public abstract class NetworkCallRunnable<R> {

    public void onPreExecute() {}

    public abstract R doBackgroundCall() throws RetrofitError;

    public abstract void onSuccess(R result);

    public abstract void onError(RetrofitError re);

    public void onFinished() {}

}