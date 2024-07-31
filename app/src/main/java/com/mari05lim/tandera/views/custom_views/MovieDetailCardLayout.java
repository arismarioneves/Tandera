package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class MovieDetailCardLayout extends FrameLayout {

    private final View mTitleLayout;
    private final TextView mTitleTextView;
    private final TextView mSeeMoreTextView;
    private final LinearLayout mCardContent;

    public MovieDetailCardLayout(Context context) {
        this(context, null);
    }

    public MovieDetailCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieDetailCardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.include_movie_detail_card, this, true);

        mTitleLayout = findViewById(R.id.card_header);
        mTitleTextView = mTitleLayout.findViewById(R.id.title);
        mSeeMoreTextView = mTitleLayout.findViewById(R.id.textview_see_more);
        mCardContent = findViewById(R.id.card_content_holder);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieDetailCardLayout);
        final String title = a.getString(R.styleable.MovieDetailCardLayout_title);
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
        a.recycle();
    }

    public void setSeeMoreVisibility(boolean visible) {
        mSeeMoreTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setSeeMoreOnClickListener(OnClickListener listener) {
        mTitleLayout.setOnClickListener(listener);
    }

    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    public void setTitle(int titleResId) {
        setTitle(getResources().getString(titleResId));
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mCardContent != null) {
            mCardContent.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

}