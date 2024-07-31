package com.mari05lim.tandera.util;

import android.content.res.Resources;

/**
 * DEV Mari05liM
 */
public class TanderaProgressBarUtils {

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}