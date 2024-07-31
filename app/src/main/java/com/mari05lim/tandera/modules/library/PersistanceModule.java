package com.mari05lim.tandera.modules.library;

import android.content.Context;

import com.mari05lim.tandera.qualifiers.AppContext;
import com.mari05lim.tandera.qualifiers.FilesDirectory;
import com.mari05lim.tandera.util.AndroidFileManager;
import com.mari05lim.tandera.model.repository.MovieRepository;
import com.mari05lim.tandera.model.repository.ShowRepository;
import com.mari05lim.tandera.model.sqlite.SQLiteHelper;
import com.mari05lim.tandera.model.util.FileManager;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module(
    library = true,
    includes = {
        ContextModule.class,
        UtilModule.class
    }
)
public class PersistanceModule {

    @Provides @Singleton
    public FileManager provideFileManager(@FilesDirectory File file) {
        return new AndroidFileManager(file);
    }

    @Provides @Singleton
    public SQLiteHelper getDatabaseHelper(@AppContext Context context) {
        return new SQLiteHelper(context);
    }

    @Provides @Singleton
    public MovieRepository getMovieDatabaseHelper(SQLiteHelper databaseHelper) {
        return new MovieRepository(databaseHelper);
    }

    @Provides @Singleton
    public ShowRepository getShowDatabaseHelper(SQLiteHelper databaseHelper) {
        return new ShowRepository(databaseHelper);
    }

}