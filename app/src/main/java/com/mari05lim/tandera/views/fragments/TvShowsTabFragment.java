package com.mari05lim.tandera.views.fragments;

import androidx.fragment.app.Fragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.presenters.TabShowsPresenter;
import com.mari05lim.tandera.mvp.views.TvShowsTabView;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.util.StringUtils;
import com.mari05lim.tandera.views.fragments.base.BaseTabFragment;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.network.NetworkError;
import com.mari05lim.tandera.model.util.MoviesCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * DEV Mari05liM
 */
public class TvShowsTabFragment extends BaseTabFragment<BaseTabFragment.TabPagerAdapter> implements TvShowsTabView {

    @InjectPresenter
    TabShowsPresenter mPresenter;

    private List<ShowTabs> mTabs;

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.attachUiToPresenter(this);
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarSubtitle(subtitle);
        }
    }

    @Override
    public void setData(List<ShowTabs> data) {
        Preconditions.checkNotNull(data, "tabs cannot be null");
        mTabs = data;

        if (getAdapter().getCount() != mTabs.size()) {
            ArrayList<Fragment> fragments = new ArrayList<>();
            for (int i = 0; i < mTabs.size(); i++) {
                fragments.add(createFragmentForTab(mTabs.get(i)));
            }
            setFragments(fragments);
        }
    }

    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void onRefreshData(boolean visible) {

    }

    @Override
    public UiView.TanderaQueryType getQueryType() {
        return UiView.TanderaQueryType.SHOWS_TAB;
    }

    @Override
    protected TabPagerAdapter setupAdapter() {
        return new TabPagerAdapter(getChildFragmentManager());
    }

    @Override
    protected String getTabTitle(int position) {
        if (!MoviesCollections.isEmpty(mTabs)) {
            return getResources().getString(StringUtils.getShowsStringResId(mTabs.get(position)));
        }
        return null;
    }

    private Fragment createFragmentForTab(UiView.ShowTabs tab) {
        switch (tab) {
            case POPULAR:
                return Fragment.instantiate(MoviesApp.get().getAppContext(), PopularShowsFragment.class.getName());
            case ON_THE_AIR:
                return Fragment.instantiate(MoviesApp.get().getAppContext(), OnTheAirShowsFragment.class.getName());
        }
        return null;
    }

}