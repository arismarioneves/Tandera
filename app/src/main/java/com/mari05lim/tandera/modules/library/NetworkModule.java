package com.mari05lim.tandera.modules.library;

import android.content.Context;

import com.mari05lim.tandera.qualifiers.AppContext;
import com.mari05lim.tandera.qualifiers.CacheDirectory;
import com.mari05lim.tandera.util.TanderaTmdb;
import com.mari05lim.tandera.model.Constants;
import com.uwetrottmann.tmdb.Tmdb;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module(
        library = true,
        includes = ContextModule.class
)
public class NetworkModule {

   @Provides @Singleton
    public Tmdb provideTmdbClient(@AppContext Context context) {
        Tmdb tmdb = new TanderaTmdb(context);
        tmdb.setApiKey(Constants.TMDB_API_KEY);
        tmdb.setIsDebug(Constants.DEBUG_NETWORK);
        return tmdb;
    }

    @Provides @Singleton @CacheDirectory
    public File provideHttpCacheLocation(@AppContext Context context) {
        return context.getCacheDir();
    }

}