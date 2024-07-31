package com.mari05lim.tandera.views.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.mvp.presenters.ListWatchedPresenter;
import com.mari05lim.tandera.mvp.views.WatchedListView;
import com.mari05lim.tandera.views.activities.BaseNavigationActivity;
import com.mari05lim.tandera.views.adapters.WatchedGridAdapter;
import com.mari05lim.tandera.views.custom_views.recyclerview.AutofitGridLayoutManager;
import com.mari05lim.tandera.views.custom_views.recyclerview.RecyclerInsetsDecoration;
import com.mari05lim.tandera.views.fragments.base.BaseGridFragment;
import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class WatchedFragment extends BaseGridFragment<WatchedGridAdapter.WatchedItemViewHolder, Watchable> implements WatchedListView {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerView().setHasFixedSize(true);
        int width = (int)getActivity().getResources().getDimension(R.dimen.saved_grid_item_width);
        getRecyclerView().setLayoutManager(new AutofitGridLayoutManager(getActivity(), width , getAdapter()));
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity(), NavigationGridType.WATCHED));
        getRecyclerView().disableLoadmore();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_grid_recycler_with_toolbar;
    }

    @Override
    public TanderaQueryType getQueryType() {
        return TanderaQueryType.WATCHED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view, int position) {
        Watchable item = mAdapter.getObjects().get(position);
        showWatchableDetail(item, view);
    }

    @Override
    public void onPopupMenuClick(View view, int position) {
    }

    @Override
    public void showWatchableDetail(Watchable item, View container) {
        Preconditions.checkNotNull(item, "Watchable cannot be null");
        Preconditions.checkNotNull(item.getTmdbId(), "watchable id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            switch (item.getWatchableType()) {
                case MOVIE : {
                    display.startMovieDetailActivity(String.valueOf(item.getTmdbId()), null);
                    break;
                }
                case TV_SHOW: {
                    display.startTvDetailActivity(String.valueOf(item.getTmdbId()), null);
                    break;
                }
            }

        }
    }

    @Override
    protected UltimateGridLayoutAdapter<Watchable, WatchedGridAdapter.WatchedItemViewHolder> createAdapter(List<Watchable> data) {
        return new WatchedGridAdapter(data, this);
    }

    @InjectPresenter
    ListWatchedPresenter mPresenter;

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onEmptyViewShow(View mView) {
        ImageView icon = mView.findViewById(R.id.empty_screen_icon);
        icon.setImageResource(R.drawable.ic_library_add);
        TextView title = mView.findViewById(R.id.empty_screen_title);
        title.setText(R.string.empty_library_title);
        TextView body = mView.findViewById(R.id.empty_screen_body);
        body.setText(R.string.empty_library_body);
        TextView action = mView.findViewById(R.id.empty_screen_action);
        action.setText(R.string.explore);
        action.setOnClickListener(v -> ((BaseNavigationActivity)getActivity()).setMoviesItemNavigationView());
    }

}