package com.mari05lim.tandera.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.ShowWrapper;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface ListTvShowsView extends BaseListView<ShowWrapper> {

    void showItemDetail(ShowWrapper item, View ui);
    void showContextMenu(ShowWrapper tvShow);

}