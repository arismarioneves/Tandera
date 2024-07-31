package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import com.google.android.material.navigation.NavigationView;
import android.util.AttributeSet;

import com.mari05lim.tandera.util.UiUtils;

/**
 * DEV Mari05liM
 */
public class MMNavigationView extends NavigationView {

    public MMNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void inflateMenu(int paramInt)
    {
        super.inflateMenu(paramInt);
        UiUtils.getInstance().applyFontToMenu(getMenu(), getContext());
    }

}