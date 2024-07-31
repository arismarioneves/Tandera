package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.CreditWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.Credits;

import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchShowCreditsRunnable extends BaseRunnable<Credits> {

    private final int mId;

    public FetchShowCreditsRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Credits doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvService().credits(mId, getCountryProvider().getLetterLanguageCode());
    }

    @Override
    public void onSuccess(Credits result) {
        ShowWrapper show = mState.getTvShow(mId);

        if (show != null) {
            if (!MoviesCollections.isEmpty(result.cast)) {
                //Cast list should be mapped due to entity mapper
                List<CreditWrapper> cast = getEntityMapper().mapCastCredits(result.cast);
                Collections.sort(cast);
                show.setCast(cast);
            }

            if (!MoviesCollections.isEmpty(result.crew)) {
                //Crew list should be mapped due to entity mapper
                List<CreditWrapper> crew = getEntityMapper().mapCrewCredits(result.crew);
                Collections.sort(crew);
                show.setCrew(crew);
            }

            getEventBus().post(new MoviesState.TvShowCastItemsUpdatedEvent(getCallingId(), show));
        }
    }

    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        ShowWrapper show  = mState.getTvShow(mId);
        if (show != null) {
            getEventBus().post(new MoviesState.TvShowCastItemsUpdatedEvent(getCallingId(), show));
        }
    }

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowCreditLoadingProgressEvent(getCallingId(), show);
    }

}