package com.mari05lim.tandera.views.fragments;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mari05lim.tandera.mvp.presenters.ListMoviesPresenter;
import com.mari05lim.tandera.views.fragments.base.MoviesGridFragment;
import com.mari05lim.tandera.model.Display;

/**
 * DEV Mari05liM
 */
public class PopularMoviesFragment extends MoviesGridFragment {

    @InjectPresenter
    ListMoviesPresenter TanderaPresenter;

    @Override
    protected void attachUiToPresenter() {
        TanderaPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        TanderaPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void onRefreshData(boolean visible) {
        super.onRefreshData(visible);
        TanderaPresenter.refresh(this, getQueryType());
    }

    @Override
    public TanderaQueryType getQueryType() {
        return TanderaQueryType.POPULAR_MOVIES;
    }

}