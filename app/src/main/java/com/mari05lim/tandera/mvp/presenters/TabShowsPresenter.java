package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.TvShowsTabView;
import com.mari05lim.tandera.mvp.views.UiView;

import java.util.Arrays;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class TabShowsPresenter extends MvpPresenter<TvShowsTabView> {

    public TabShowsPresenter() {
        super();
    }

    public void attachUiToPresenter(TvShowsTabView view) {
        super.attachView(view);
        getViewState().updateDisplayTitle(MoviesApp.get().getStringFetcher().getString(R.string.shows_title));
        populateUi();
    }

    private void populateUi() {
        UiView.ShowTabs[] tabs = {UiView.ShowTabs.POPULAR, UiView.ShowTabs.ON_THE_AIR};
        getViewState().setData(Arrays.asList(tabs));
    }

}