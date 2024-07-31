package com.mari05lim.tandera.model.tasks;

import android.text.TextUtils;

import com.mari05lim.tandera.model.entities.SeasonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.TvSeason;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchDetailTvSeasonRunnable extends BaseRunnable<TvSeason> {

    private final int showId;
    private final int seasonNumber;

    public FetchDetailTvSeasonRunnable(int callingId, int showId, int seasonNumber) {
        super(callingId);
        this.showId = showId;
        this.seasonNumber = seasonNumber;
    }

    @Override
    public TvSeason doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvSeasonsService().season(showId, seasonNumber, getCountryProvider().getLetterLanguageCode());
    }

    @Override
    public void onSuccess(TvSeason result) {
        ShowWrapper show = mState.getTvShow(showId);
        SeasonWrapper season = show.getSeason(String.valueOf(seasonNumber));
        season.markFullFetchCompleted();
        if (!TextUtils.isEmpty(result.name)) {
            season.setTitle(result.name);
        }

        if (!TextUtils.isEmpty(result.overview)) {
            season.setOverview(result.overview);
        }

        if (!MoviesCollections.isEmpty(result.episodes)) {
            season.setEpisodes(result.episodes);
        }

        getEventBus().post(new MoviesState.TvShowSeasonUpdatedEvent(getCallingId(), String.valueOf(showId), season));
    }

    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        ShowWrapper show = mState.getTvShow(showId);
        SeasonWrapper season = show.getSeason(String.valueOf(seasonNumber));
        if (season != null) {
            getEventBus().post(new MoviesState.TvShowSeasonUpdatedEvent(getCallingId(), String.valueOf(showId), season));
        }
    }

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowTvSeasonLoadingProgressEvent(getCallingId(), show);
    }

}