package com.mari05lim.tandera.modules.library;

import android.content.Context;

import com.mari05lim.tandera.qualifiers.AppContext;
import com.mari05lim.tandera.qualifiers.Database;
import com.mari05lim.tandera.qualifiers.GeneralPurpose;
import com.mari05lim.tandera.util.AndroidStringFetcher;
import com.mari05lim.tandera.util.TanderaBackgroundExecutor;
import com.mari05lim.tandera.util.TanderaCountryProvider;
import com.mari05lim.tandera.model.util.BackgroundExecutor;
import com.mari05lim.tandera.model.util.CountryProvider;
import com.mari05lim.tandera.model.util.StringFetcher;
import com.squareup.otto.Bus;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module(
    includes=ContextModule.class,
    library = true
)
public class UtilModule {

    @Provides
    @Singleton
    public Bus provideEventBus() {
        return new Bus();
    }

    @Provides @Singleton
    public CountryProvider provideCountryProvider(@AppContext Context context) {
        return new TanderaCountryProvider(context);
    }

    @Provides @Singleton @GeneralPurpose
    public BackgroundExecutor provideMultiThreadExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new TanderaBackgroundExecutor(Executors.newFixedThreadPool(numberCores * 2 + 1));
    }

    @Provides @Singleton @Database
    public BackgroundExecutor provideDatabaseThreadExecutor() {
        return new TanderaBackgroundExecutor(Executors.newSingleThreadExecutor());
    }

    @Provides @Singleton
    public StringFetcher provideStringFetcher(@AppContext Context context) {
        return new AndroidStringFetcher(context);
    }

}