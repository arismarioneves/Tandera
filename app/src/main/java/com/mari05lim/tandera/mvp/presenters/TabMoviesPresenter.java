package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.MoviesTabView;
import com.mari05lim.tandera.mvp.views.UiView;

import java.util.Arrays;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class TabMoviesPresenter extends MvpPresenter<MoviesTabView> {

    public TabMoviesPresenter() {
        super();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void attachUiToPresenter(MoviesTabView view) {
        super.attachView(view);
        getViewState().updateDisplayTitle(MoviesApp.get().getStringFetcher().getString(R.string.movies_title));
        populateUi();
    }

    private void populateUi() {
//            UiView.MovieTabs[] tabs = {UiView.MovieTabs.POPULAR, UiView.MovieTabs.IN_THEATRES, UiView.MovieTabs.UPCOMING};
            UiView.MovieTabs[] tabs = {UiView.MovieTabs.POPULAR, UiView.MovieTabs.IN_THEATRES};
            getViewState().setData(Arrays.asList(tabs));
    }

}