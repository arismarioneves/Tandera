package com.mari05lim.tandera.mvp.views;

import com.arellomobile.mvp.GenerateViewState;
import com.arellomobile.mvp.MvpView;
import com.mari05lim.tandera.model.entities.ShowWrapper;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface TvShowWatchedView extends MvpView {

    void updateShowWatched(ShowWrapper item, int position);

}