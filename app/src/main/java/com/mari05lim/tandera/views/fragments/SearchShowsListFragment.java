package com.mari05lim.tandera.views.fragments;

import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.mari05lim.tandera.mvp.presenters.ListTvShowsPresenter;
import com.mari05lim.tandera.mvp.views.ListTvShowsView;
import com.mari05lim.tandera.views.adapters.ShowListAdapter;
import com.mari05lim.tandera.views.fragments.base.BaseListFragment;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.ShowWrapper;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class SearchShowsListFragment extends BaseListFragment<ShowListAdapter.WatchableListViewHolder, ShowWrapper> implements ListTvShowsView {

    @InjectPresenter
    ListTvShowsPresenter mPresenter;

    @Override
    public TanderaQueryType getQueryType() {
        return TanderaQueryType.SEARCH_SHOWS;
    }

    @Override
    protected easyRegularAdapter<ShowWrapper, ShowListAdapter.WatchableListViewHolder> createAdapter(List<ShowWrapper> data) {
        return new ShowListAdapter(data, this);
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onClick(View view, int position) {
        ShowWrapper item = mAdapter.getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void showItemDetail(ShowWrapper tvShow, View ui) {
        Preconditions.checkNotNull(tvShow, "tv cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (tvShow.getTmdbId() != null) {
                display.startTvDetailActivity(String.valueOf(tvShow.getTmdbId()), null);
            }
        }
    }

    @Override
    public void showContextMenu(ShowWrapper tvShow) {

    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }

}