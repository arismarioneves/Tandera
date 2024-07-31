package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.util.FontManager;

import javax.inject.Inject;

/**
 * DEV Mari05liM
 */
public class TanderaToolbar extends Toolbar {

    @Inject
    FontManager mFontManager;

    public TanderaToolbar(Context context) {
        this(context, null);
    }

    public TanderaToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TanderaToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MoviesApp.from(context).inject(this);
    }

    public TextView getToolbarTitle(Toolbar paramToolbar)
    {
        int i = paramToolbar.getChildCount();
        for (int j = 0; j < i; j++)
        {
            View localView = paramToolbar.getChildAt(j);
            if ((localView instanceof TextView))
                return (TextView)localView;
        }
        throw new IllegalStateException("Toolbar title not found!");
    }

    public void setToolbarTitleTypeface() {
        TextView localTextView = getToolbarTitle(this);
        if (localTextView != null)
        {
            mFontManager.setFont(localTextView);
        }
    }

}