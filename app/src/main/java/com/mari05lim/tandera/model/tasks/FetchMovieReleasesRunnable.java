package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.util.CountryProvider;
import com.uwetrottmann.tmdb.entities.Releases;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchMovieReleasesRunnable extends BaseRunnable<Releases> {

    @Inject
    CountryProvider mCountryProvider;
    private final int mId;

    public FetchMovieReleasesRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Releases doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().releases(mId);
    }

    @Override
    public void onSuccess(Releases result) {
        final String countryCode = mCountryProvider.getLetterCountryCode();
        MovieWrapper movie = mState.getMovie(mId);
        if (movie != null) {

            movie.updateReleases(result, countryCode);

            //getDbHelper().put(movie);
            getEventBus().post(new MoviesState.MovieReleasesUpdatedEvent(getCallingId(), movie));
        }

    }
}