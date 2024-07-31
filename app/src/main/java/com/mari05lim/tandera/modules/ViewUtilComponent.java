package com.mari05lim.tandera.modules;

import android.content.res.AssetManager;

import com.mari05lim.tandera.modules.library.ContextModule;
import com.mari05lim.tandera.modules.library.UtilModule;
import com.mari05lim.tandera.util.FlagUrlProvider;
import com.mari05lim.tandera.util.FontManager;
import com.mari05lim.tandera.views.custom_views.AutofitTextView;
import com.mari05lim.tandera.views.custom_views.ExpandableTextView;
import com.mari05lim.tandera.views.custom_views.TanderaButton;
import com.mari05lim.tandera.views.custom_views.TanderaEditText;
import com.mari05lim.tandera.views.custom_views.TanderaTextView;
import com.mari05lim.tandera.views.custom_views.TanderaToolbar;
import com.mari05lim.tandera.views.custom_views.TanderaToolbarLayout;
import com.mari05lim.tandera.views.fragments.MovieDetailFragment;
import com.mari05lim.tandera.views.fragments.TvShowDetailFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module(
    includes = {
        UtilModule.class,
        ContextModule.class
    }
    ,
    injects = {
        TanderaTextView.class,
        TanderaEditText.class,
        TanderaButton.class,
        ExpandableTextView.class,
        TanderaToolbar.class,
        AutofitTextView.class,
        TanderaToolbarLayout.class,
        MovieDetailFragment.class,
        TvShowDetailFragment.class
    }
)
public class ViewUtilComponent {

    @Provides @Singleton
    public FontManager provideFontManager(AssetManager assetManager) {
            return new FontManager(assetManager);
    }

    @Provides @Singleton
    public FlagUrlProvider getFlagUrlProvider() {
            return new FlagUrlProvider();
    }

}