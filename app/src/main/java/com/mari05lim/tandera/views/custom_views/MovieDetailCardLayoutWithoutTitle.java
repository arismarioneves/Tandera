package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class MovieDetailCardLayoutWithoutTitle extends FrameLayout {

    private final LinearLayout mCardContent;

    public MovieDetailCardLayoutWithoutTitle(Context context) {
        this(context, null);
    }

    public MovieDetailCardLayoutWithoutTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieDetailCardLayoutWithoutTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.include_movie_detail_card_without_title, this, true);

        mCardContent = findViewById(R.id.card_content_holder);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieDetailCardLayoutWithoutTitle);
        a.recycle();
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