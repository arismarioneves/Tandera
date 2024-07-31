package com.mari05lim.tandera.modules.library;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.qualifiers.AppContext;
import com.mari05lim.tandera.qualifiers.FilesDirectory;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module(
        library = true
)
public class ContextModule {

    private final Context mAppContext;

    public ContextModule(Context context) {
        mAppContext = Preconditions.checkNotNull(context, "context can not be null");
    }

    @Provides @AppContext
    public Context provideAppContext() {return  mAppContext;}

    @Provides @FilesDirectory
    public File providePrivateFileDirectory() {
        return mAppContext.getFilesDir();
    }

    @Provides @Singleton
    public AssetManager provideAssetManager() {
        return mAppContext.getAssets();
    }

}