package com.mari05lim.tandera.views.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mari05lim.tandera.R;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * DEV Mari05liM
 */
public class UserHeaderLayout extends FrameLayout {


    @BindView(R.id.watched_movies_txt)
    TextView mWatchedMoviesCount;

    @BindView(R.id.watched_shows_txt)
    TextView mWatchedShowsCount;

    int[] watchedItemsCount;

    public UserHeaderLayout(Context context) {
        super(context);
        init(context);
    }

    public UserHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode())
            return;
        ButterKnife.bind(this, this);
    }

    private void init(Context context) {
       // LayoutInflater.from(context).inflate(R.layout.item_account_header, this, false);
        View.inflate(getContext(), R.layout.item_account_header, this);
    }

    public static UserHeaderLayout inflate(Context paramContext, ViewGroup paramViewGroup)
    {
        return (UserHeaderLayout) LayoutInflater.from(paramContext).inflate(R.layout.item_account_header, paramViewGroup, false);
    }

    /*public void updateWatchedData(int[] data) {
        watchedItemsCount = data;
        mWatchedMoviesCount.setText(String.valueOf(watchedItemsCount[0]));
        mWatchedShowsCount.setText(String.valueOf(watchedItemsCount[1]));
    }*/

}