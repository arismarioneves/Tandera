package com.mari05lim.tandera.mvp.views;

import android.graphics.Bitmap;
import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.SeasonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;

import java.util.List;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface TvShowDetailView extends MvpLceView<ShowWrapper> {

    void setSeasons(List<SeasonWrapper> data);

    void showTvShowImages(ShowWrapper movie);

    void showTvShowCreditsDialog(TanderaQueryType queryType);

    void showSeasonDetail(SeasonWrapper season);

    void updateDisplaySubtitle(CharSequence subtitle);

    void showPersonDetail(PersonWrapper person, View ui);

    void setSeasonPoster(Bitmap bitmap);

}