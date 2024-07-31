package com.mari05lim.tandera.views.fragments.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.marshalchen.ultimaterecyclerview.ui.emptyview.emptyViewOnShownListener;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.BaseListView;
import com.mari05lim.tandera.views.custom_views.recyclerview.TanderaRecyclerView;
import com.mari05lim.tandera.views.listeners.RecyclerItemClickListener;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.network.NetworkError;

import java.io.Serializable;
import java.util.List;

/**
 * DEV Mari05liM
 */
public abstract class RecyclerFragment<VH extends UltimateRecyclerviewViewHolder, M extends Serializable>
        extends BaseMvpFragment implements BaseListView<M>, RecyclerItemClickListener, emptyViewOnShownListener {

    protected TanderaRecyclerView mUltimateRecyclerView;

    protected boolean  status_progress = false;

    protected easyRegularAdapter<M, VH> mAdapter = null;

    //protected abstract AD createAdapter(List<M> data);

    protected TanderaRecyclerView getRecyclerView() {
        return mUltimateRecyclerView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUltimateRecyclerView = view.findViewById(R.id.primary_recycler_view);
    }

    @Override
    public void setData(List<M> data) {
        if (data != null) {
                mAdapter.removeAll();
                getAdapter().insert(data);
        }
    }

    public boolean hasAdapter() {
        return mAdapter != null;
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
    public void onScrolledToBottom() {
    }

    @Override
    public void showError(NetworkError error) {
        mUltimateRecyclerView.showEmptyView();
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        mUltimateRecyclerView.setRefreshing(visible);
    }

    @Override
    public void onRefreshData(boolean visible) {
        mUltimateRecyclerView.setRefreshing(false);
        mUltimateRecyclerView.scrollVerticallyTo(0);
        mAdapter.removeAll();
    }

    protected void enableLoadMore() {
        mUltimateRecyclerView.setLoadMoreView(R.layout.secondary_progress_bar);

        mUltimateRecyclerView.setOnLoadMoreListener((itemsCount, maxLastVisiblePosition) -> {
            status_progress = true;
            Handler handler = new Handler();
            handler.postDelayed(() -> {

                onScrolledToBottom();
                status_progress = false;
            }, 500);
        });
    }

    protected void enableEmptyViewPolicy() {
         mUltimateRecyclerView.setEmptyView(R.layout.item_empty_screen, UltimateRecyclerView.EMPTY_KEEP_HEADER_AND_LOARMORE, this);
    }

    public easyRegularAdapter<M, VH> getAdapter() {
        return mAdapter;
    }

}