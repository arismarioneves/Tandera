package com.mari05lim.tandera.mvp.callbacks;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.util.MoviesCollections;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class WatchedDbLoadCallback implements ApplicationState.Callback<List<Watchable>> {

    @Override
    public void onFinished(List<Watchable> result) {
        MoviesApp.get().getState().setWatched(result);
        if (!MoviesCollections.isEmpty(result)) {
            for (Watchable item : result) {
                switch (item.getWatchableType()) {
                    case MOVIE:
                        MoviesApp.get().getState().putMovie((MovieWrapper)item);
                        break;
                    case TV_SHOW:
                        MoviesApp.get().getState().putShow((ShowWrapper)item);
                        break;

                }
            }
        }

        MoviesApp.get().getState().setPopulatedWatchedFromDb(true);
    }

}