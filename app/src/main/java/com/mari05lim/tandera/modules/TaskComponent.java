package com.mari05lim.tandera.modules;

import com.mari05lim.tandera.modules.library.ContextModule;
import com.mari05lim.tandera.modules.library.NetworkModule;
import com.mari05lim.tandera.modules.library.PersistanceModule;
import com.mari05lim.tandera.modules.library.StateModule;
import com.mari05lim.tandera.modules.library.UtilModule;
import com.mari05lim.tandera.model.tasks.ClearWatchedRunnable;
import com.mari05lim.tandera.model.tasks.FetchConfigurationRunnable;
import com.mari05lim.tandera.model.tasks.FetchDetailMovieRunnable;
import com.mari05lim.tandera.model.tasks.FetchDetailTvSeasonRunnable;
import com.mari05lim.tandera.model.tasks.FetchDetailTvShowRunnable;
import com.mari05lim.tandera.model.tasks.FetchInTheatresRunnable;
import com.mari05lim.tandera.model.tasks.FetchMovieCreditsRunnable;
import com.mari05lim.tandera.model.tasks.FetchMovieImagesRunnable;
import com.mari05lim.tandera.model.tasks.FetchMovieReleasesRunnable;
import com.mari05lim.tandera.model.tasks.FetchMovieTrailersRunnable;
import com.mari05lim.tandera.model.tasks.FetchOnTheAirShowsRunnable;
import com.mari05lim.tandera.model.tasks.FetchPersonCreditsRunnable;
import com.mari05lim.tandera.model.tasks.FetchPersonRunnable;
import com.mari05lim.tandera.model.tasks.FetchPopularMoviesRunnable;
import com.mari05lim.tandera.model.tasks.FetchPopularShowsRunnable;
import com.mari05lim.tandera.model.tasks.FetchRelatedMoviesRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchMovieRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchPeopleRunnable;
import com.mari05lim.tandera.model.tasks.FetchSearchShowRunnable;
import com.mari05lim.tandera.model.tasks.FetchShowCreditsRunnable;
import com.mari05lim.tandera.model.tasks.FetchUpcomingMoviesRunnable;
import com.mari05lim.tandera.model.tasks.FetchWatchedRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntitySeenRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntityUnseenRunnable;

import dagger.Module;

/**
 * DEV Mari05liM
 */
@Module(
    injects = {
        FetchDetailMovieRunnable.class,
        FetchMovieCreditsRunnable.class,
        FetchMovieImagesRunnable.class,
        FetchMovieReleasesRunnable.class,
        FetchMovieTrailersRunnable.class,
        FetchPersonCreditsRunnable.class,
        FetchPersonRunnable.class,
        FetchPopularMoviesRunnable.class,
        FetchInTheatresRunnable.class,
        FetchUpcomingMoviesRunnable.class,
        FetchRelatedMoviesRunnable.class,
        FetchConfigurationRunnable.class,
        FetchPopularShowsRunnable.class,
        FetchOnTheAirShowsRunnable.class,
        FetchSearchMovieRunnable.class,
        FetchSearchPeopleRunnable.class,
        FetchSearchShowRunnable.class,
        FetchDetailTvShowRunnable.class,
        FetchDetailTvSeasonRunnable.class,
        FetchShowCreditsRunnable.class,
        FetchWatchedRunnable.class,
        MarkEntitySeenRunnable.class,
        MarkEntityUnseenRunnable.class,
        ClearWatchedRunnable.class
    },
    includes = {
        ContextModule.class,
        PersistanceModule.class,
        StateModule.class,
        UtilModule.class,
        NetworkModule.class
    }
)
public class TaskComponent {

}