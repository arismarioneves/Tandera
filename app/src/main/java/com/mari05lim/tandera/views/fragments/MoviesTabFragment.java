package com.mari05lim.tandera.views.fragments;

import androidx.fragment.app.Fragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.presenters.TabMoviesPresenter;
import com.mari05lim.tandera.mvp.views.MoviesTabView;
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
public class MoviesTabFragment extends BaseTabFragment<BaseTabFragment.TabPagerAdapter> implements MoviesTabView {

    @InjectPresenter
    TabMoviesPresenter mPresenter;

    private List<UiView.MovieTabs> mTabs;

    @Override
    protected void attachUiToPresenter() {
        mPresenter.attachUiToPresenter(this);
    }

    @Override
    public UiView.TanderaQueryType getQueryType() {
        return UiView.TanderaQueryType.MOVIES_TAB;
    }

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarSubtitle(subtitle);
        }
    }

    @Override
    protected TabPagerAdapter setupAdapter() {
        return new TabPagerAdapter(getChildFragmentManager());
    }

    @Override
    public void setData(List<MovieTabs> data) {
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
    protected String getTabTitle(int position) {
        if (!MoviesCollections.isEmpty(mTabs)) {
            return getActivity().getResources().getString(StringUtils.getTabTitle(mTabs.get(position)));
        }
        return null;
    }

    private Fragment createFragmentForTab(UiView.MovieTabs tab) {
        switch (tab) {
            case POPULAR:
                return Fragment.instantiate(MoviesApp.get().getAppContext(), PopularMoviesFragment.class.getName());
            case IN_THEATRES:
                return Fragment.instantiate(MoviesApp.get().getAppContext(), InTheatresMoviesFragment.class.getName());
//            case UPCOMING:
//                return Fragment.instantiate(MoviesApp.get().getAppContext(), UpcomingMoviesFragment.class.getName());
        }
        return null;
    }

}