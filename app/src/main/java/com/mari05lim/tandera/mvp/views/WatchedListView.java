package com.mari05lim.tandera.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.Watchable;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface WatchedListView extends BaseListView<Watchable> {

    void showWatchableDetail(Watchable item, View container);

}