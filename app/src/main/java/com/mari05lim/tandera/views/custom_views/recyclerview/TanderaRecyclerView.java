package com.mari05lim.tandera.views.custom_views.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class TanderaRecyclerView extends UltimateRecyclerView {

    private boolean isRecyclerAnimated;

    public TanderaRecyclerView(Context context) {
        super(context);
    }

    public TanderaRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TanderaRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        super.initAttrs(attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TanderaRecyclerView);
        try {
            isRecyclerAnimated = typedArray.getBoolean(R.styleable.TanderaRecyclerView_recyclerSliding, false);
        } finally {
            typedArray.recycle();
        }
    }

    protected void applyRecyclerAnimation() {
        if (isRecyclerAnimated) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mSwipeRefreshLayout.removeView(mRecyclerView);
            View animationGridRecyclerView = inflater.inflate(R.layout.recyclerview_layout_grid, mSwipeRefreshLayout, true);
            mRecyclerView = (AnimationGridRecyclerView) animationGridRecyclerView.findViewById(R.id.recycler_view);
        }
    }

    @Override
    protected void setScrollbars() {
        super.setScrollbars();
        applyRecyclerAnimation();
    }

    @Override
    public boolean showEmptyView() {
        if (mEmpty != null && mEmptyView != null ) {
                mEmpty.setVisibility(View.VISIBLE);
                if (mEmptyViewListener != null) {
                    mEmptyViewListener.onEmptyViewShow(mEmptyView);
                }
            return true;
        } else {
            Log.d(VIEW_LOG_TAG, "it is unable to show empty view");
            return false;
        }
    }

}