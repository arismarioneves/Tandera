package com.mari05lim.tandera.modules.library;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.mari05lim.tandera.model.state.ApplicationState;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module (
    library =  true,
    includes = UtilModule.class
)
public class StateModule {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Provides @Singleton
    public ApplicationState provideApplicationState(Bus bus) {
        return new ApplicationState(bus);
    }

}