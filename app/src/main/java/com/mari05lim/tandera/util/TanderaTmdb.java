package com.mari05lim.tandera.util;

import android.content.Context;

import com.uwetrottmann.tmdb.Tmdb;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * DEV Mari05liM
 */
public class TanderaTmdb extends Tmdb {

    private final Context context;

    public TanderaTmdb(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected RestAdapter.Builder newRestAdapterBuilder() {
        return new RestAdapter.Builder().setClient(
                new OkClient(TanderaServiceUtils.getCachingOkHttpClient(context)));
    }

}