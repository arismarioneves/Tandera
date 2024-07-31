package com.mari05lim.tandera.views.fragments;

import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.mari05lim.tandera.mvp.presenters.ListMoviesPresenter;
import com.mari05lim.tandera.mvp.views.ListMoviesView;
import com.mari05lim.tandera.views.adapters.MovieListAdapter;
import com.mari05lim.tandera.views.fragments.base.BaseListFragment;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.MovieWrapper;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class SearchMoviesListFragment extends BaseListFragment<MovieListAdapter.WatchableListViewHolder, MovieWrapper> implements ListMoviesView {

    @InjectPresenter
    ListMoviesPresenter mPresenter;

    @Override
    public TanderaQueryType getQueryType() {
        return TanderaQueryType.SEARCH_MOVIES;
    }

    @Override
    protected easyRegularAdapter<MovieWrapper, MovieListAdapter.WatchableListViewHolder> createAdapter(List<MovieWrapper> data) {
        return new MovieListAdapter(data, this);
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
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void onClick(View view, int position) {
        MovieWrapper item = mAdapter.getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void showItemDetail(MovieWrapper movie, View view) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), null);
            }
        }
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }

}