package com.mari05lim.tandera.views.fragments.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.mari05lim.tandera.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DEV Mari05liM
 */
public abstract class BaseGridFragment<VH extends UltimateRecyclerviewViewHolder, M extends Serializable>
        extends RecyclerFragment<VH, M> {

    protected abstract UltimateGridLayoutAdapter<M, VH> createAdapter(List<M> data);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerView().setHasFixedSize(false);
        enableEmptyViewPolicy();
        enableLoadMore();
        mAdapter = createAdapter(new ArrayList<M>());
        mUltimateRecyclerView.setItemViewCacheSize(mAdapter.getAdditionalItems());
        ((UltimateGridLayoutAdapter)getAdapter()).setSpanColumns(2);
        mUltimateRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onEmptyViewShow(View mView) {
        ImageView icon = mView.findViewById(R.id.empty_screen_icon);
        icon.setImageResource(R.drawable.ic_signal_wifi_off);
        TextView title = mView.findViewById(R.id.empty_screen_title);
        title.setText(R.string.error_no_connection);
        TextView body = mView.findViewById(R.id.empty_screen_body);
        body.setText(R.string.error_no_connection_body);
        TextView action = mView.findViewById(R.id.empty_screen_action);
        action.setText(R.string.menu_reload);
        action.setOnClickListener(v -> onRefreshData(true));
    }

}