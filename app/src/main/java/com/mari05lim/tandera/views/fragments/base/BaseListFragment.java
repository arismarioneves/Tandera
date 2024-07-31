package com.mari05lim.tandera.views.fragments.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.mari05lim.tandera.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DEV Mari05liM
 */
public abstract class BaseListFragment<VH extends UltimateRecyclerviewViewHolder, M extends Serializable>
        extends RecyclerFragment<VH, M> {

    protected abstract easyRegularAdapter<M, VH> createAdapter(List<M> data);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_search);
        if (search != null)
            search.setVisible(false);
        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (refresh != null)
            refresh.setVisible(false);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_list_recycler_with_toolbar;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getRecyclerView().enableLoadMoreView(false);
        getRecyclerView().setHasFixedSize(true);
        getRecyclerView().setLayoutManager(new ScrollSmoothLineaerLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, 300));
        enableEmptyViewPolicy();
        mAdapter = createAdapter(new ArrayList<M>());
        mUltimateRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(getActivity()));
    }

    @Override
    public void onEmptyViewShow(View mView) {

    }

}