package com.mari05lim.tandera.views.fragments;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mari05lim.tandera.mvp.presenters.ListTvShowsPresenter;
import com.mari05lim.tandera.views.fragments.base.TvShowsGridFragment;
import com.mari05lim.tandera.model.Display;

/**
 * DEV Mari05liM
 */
public class PopularShowsFragment extends TvShowsGridFragment {

    @InjectPresenter
    ListTvShowsPresenter mPresenter;

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void onRefreshData(boolean visible) {
        super.onRefreshData(visible);
        mPresenter.refresh(this, getQueryType());

    }

    @Override
    public TanderaQueryType getQueryType() {
        return TanderaQueryType.POPULAR_SHOWS;
    }
}