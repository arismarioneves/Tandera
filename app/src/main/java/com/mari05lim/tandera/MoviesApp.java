package com.mari05lim.tandera;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;

import androidx.multidex.MultiDex;

import com.arellomobile.mvp.MvpFacade;
import com.google.android.gms.ads.MobileAds;
import com.mari05lim.tandera.model.entities.Entity;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.repository.MovieRepository;
import com.mari05lim.tandera.model.repository.Repository;
import com.mari05lim.tandera.model.repository.ShowRepository;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.util.BackgroundExecutor;
import com.mari05lim.tandera.model.util.Injector;
import com.mari05lim.tandera.model.util.StringFetcher;
import com.mari05lim.tandera.modules.ApplicationComponent;
import com.mari05lim.tandera.modules.TaskComponent;
import com.mari05lim.tandera.modules.ViewUtilComponent;
import com.mari05lim.tandera.modules.library.ContextModule;
import com.mari05lim.tandera.modules.library.InjectorModule;
import com.mari05lim.tandera.qualifiers.GeneralPurpose;
import com.mari05lim.tandera.util.FontManager;
import com.squareup.otto.Bus;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import dagger.ObjectGraph;

import static com.mari05lim.tandera.model.Constants.SITE_TANDERA;

/**
 * @author Mari05liM - Arismário Neves
 */
public class MoviesApp extends Application implements Injector {

    private static MoviesApp sInstance;

    @Inject
    ApplicationState mState;

    @Inject
    @GeneralPurpose
    BackgroundExecutor executor;

    @Inject
    StringFetcher mStringFetcher;

    @Inject
    FontManager mFontManager;

    @Inject
    MovieRepository mMovieRepositiry;

    @Inject
    ShowRepository mShowRepository;

    private Bus mBus;

    public MoviesApp() {
        sInstance = this;
    }

    public ApplicationState getState() {
        return mState;
    }

    public Context getAppContext() {
        return this;
    }

    public BackgroundExecutor getBackgroundExecutor() {
        return executor;
    }

    public StringFetcher getStringFetcher() {
        return mStringFetcher;
    }

    public FontManager getFontManager() {
        return mFontManager;
    }

    public Bus getBus() {
        return mBus;
    }

    public static MoviesApp from(Context context) {
        return (MoviesApp) context.getApplicationContext();
    }

    public static MoviesApp get() {
        return sInstance;
    }

    public boolean isAuthentificatedFeatureEnabled() {
        return true;
    }

    public boolean isDatabaseEnabled() {
        return true;
    }

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        //Shortcut
        AddAnotherShortcutDynamically();

        //AdMob
        MobileAds.initialize(this, initializationStatus -> {
        });

        MvpFacade.init();

        mBus = new Bus();

        mObjectGraph = ObjectGraph.create(
                new ContextModule(this),
                new ApplicationComponent(),
                new TaskComponent(),
                new ViewUtilComponent(),
                new InjectorModule(this)
        );
        mObjectGraph.inject(this);
        initRepositories();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    private void initRepositories() {
        if (sInstance.isDatabaseEnabled()) {

            initDatabaseRepositories(getState().getRepositories());
        }
    }

    private void initDatabaseRepositories(Map<Class<? extends Entity>, Repository<? extends Entity>> reposMap) {
        reposMap.put(MovieWrapper.class, mMovieRepositiry);
        reposMap.put(ShowWrapper.class, mShowRepository);
    }

    private void AddAnotherShortcutDynamically() {
        ShortcutManager shortcutManager;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);
            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "web")
                    .setShortLabel("Tandera Web")
                    .setLongLabel("Tandera plataforma Web")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_action_web))
                    .setIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(SITE_TANDERA)))
                    .build();

            shortcutManager.setDynamicShortcuts(Collections.singletonList(shortcut));
        }
    }

}