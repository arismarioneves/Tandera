package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import android.util.AttributeSet;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.util.FontManager;

import javax.inject.Inject;

/**
 * DEV Mari05liM
 */
public class TanderaToolbarLayout extends CollapsingToolbarLayout {

    @Inject
    FontManager mFontManager;

    public TanderaToolbarLayout(Context context) {
        this(context, null);
    }

    public TanderaToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TanderaToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MoviesApp.from(context).inject(this);

        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TanderaToolbarLayout);
            setCollapsedTitleTypeface(getTypeface(a.getInt(R.styleable.TanderaToolbarLayout_font_collapsed, 7)));
            setExpandedTitleTypeface(getTypeface(a.getInt(R.styleable.TanderaToolbarLayout_font_expanded, 7)));
            a.recycle();
        }
    }

    public Typeface getTypeface(final int font) {
        Typeface typeface = mFontManager.getFont(font);
        return typeface;
    }

}