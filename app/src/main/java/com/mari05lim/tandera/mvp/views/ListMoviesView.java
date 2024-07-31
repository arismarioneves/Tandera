package com.mari05lim.tandera.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.MovieWrapper;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface ListMoviesView extends BaseListView<MovieWrapper> {

    void showItemDetail(MovieWrapper item, View ui);

}