package com.mari05lim.tandera.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import androidx.collection.LruCache;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import static com.mari05lim.tandera.model.Constants.ROBOTO_CONDENSED;
import static com.mari05lim.tandera.model.Constants.ROBOTO_CONDENSED_BOLD;
import static com.mari05lim.tandera.model.Constants.ROBOTO_CONDENSED_LIGHT;

/**
 * DEV Mari05liM
 */
public class FontManager {

    public static final int FONT_ROBOTO_CONDENSED = 1;
    public static final int FONT_ROBOTO_CONDENSED_LIGHT = 2;
    public static final int FONT_ROBOTO_CONDENSED_BOLD = 3;

    private static final String ROBOTO_LIGHT_NATIVE_FONT_FAMILY = "sans-serif-light";
    private static final String ROBOTO_CONDENSED_NATIVE_FONT_FAMILY = "sans-serif-condensed";

    private final LruCache<String, Typeface> mCache;
    private final AssetManager mAssetManager;

    public FontManager(AssetManager assetManager) {
        mAssetManager = Preconditions.checkNotNull(assetManager, "assetManager cannot be null");
        mCache = new LruCache<>(3);
    }

    public Typeface getRobotoCondensed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.NORMAL);
        }
        return getTypeface(ROBOTO_CONDENSED);
    }

    public Typeface getRobotoCondensedBold() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.BOLD);
        }
        return getTypeface(ROBOTO_CONDENSED_BOLD);
    }

    public Typeface getRobotoCondensedLight() {
        return getTypeface(ROBOTO_CONDENSED_LIGHT);
    }

    private Typeface getTypeface(final String filename) {
        Typeface typeface = mCache.get(filename);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(mAssetManager, "fonts/" + filename);
            mCache.put(filename, typeface);
        }
        return typeface;
    }

    public Typeface getFont(final int fontType) {
        Typeface typeface = null;

        switch (fontType) {
            case FONT_ROBOTO_CONDENSED:
                typeface = getRobotoCondensed();
                break;
            case FONT_ROBOTO_CONDENSED_LIGHT:
                typeface = getRobotoCondensedLight();
                break;
            case FONT_ROBOTO_CONDENSED_BOLD:
                typeface = getRobotoCondensedBold();
                break;
        }

        return typeface;
    }

    public void setFont(TextView textView, final Integer fontType) {
        if (textView == null)
            return;
        textView.setTypeface(getFont(fontType));
    }

    public void setFont(TextView textView) {
        setFont(textView, FONT_ROBOTO_CONDENSED);
    }

}