package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.Videos;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchMovieTrailersRunnable extends BaseRunnable<Videos> {

    private final int mId;

    public FetchMovieTrailersRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Videos doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().videos(mId, getCountryProvider().getLetterLanguageCode());
    }

    @Override
    public void onSuccess(Videos result) {
        MovieWrapper movie = mState.getMovie(mId);

        if (movie != null) {
            movie.updateVideos(result);

            getEventBus().post(new MoviesState.MovieVideosItemsUpdatedEvent(getCallingId(), movie));
        }
    }

    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        MovieWrapper movie = mState.getMovie(mId);
        if (movie != null) {
            getEventBus().post(new MoviesState.MovieVideosItemsUpdatedEvent(getCallingId(), movie));
        }
    }

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowVideosLoadingProgressEvent(getCallingId(), show);
    }

}