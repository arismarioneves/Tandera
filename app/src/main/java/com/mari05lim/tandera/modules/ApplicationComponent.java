package com.mari05lim.tandera.modules;

import android.content.Context;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.modules.library.InjectorModule;
import com.mari05lim.tandera.modules.library.NetworkModule;
import com.mari05lim.tandera.modules.library.PersistanceModule;
import com.mari05lim.tandera.modules.library.StateModule;
import com.mari05lim.tandera.modules.library.UtilModule;
import com.mari05lim.tandera.qualifiers.AppContext;
import com.mari05lim.tandera.util.TanderaVisitManager;
import com.mari05lim.tandera.views.activities.SettingsActivity;
import com.mari05lim.tandera.views.activities.WelcomeActivity;
import com.mari05lim.tandera.model.util.VisitManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module(
    injects = {
        MoviesApp.class,
        WelcomeActivity.class,
        SettingsActivity.class
    },
    includes = {
        UtilModule.class,
        NetworkModule.class,
        StateModule.class,
        PersistanceModule.class,
        InjectorModule.class,
        ViewUtilComponent.class
    }
)
public class ApplicationComponent {

    @Provides @Singleton
    public VisitManager getVisitManager(@AppContext Context context) {
        return new TanderaVisitManager(context);
    }

}