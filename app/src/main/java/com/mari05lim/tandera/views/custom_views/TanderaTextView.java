package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.util.FontManager;

import javax.inject.Inject;

/**
 * DEV Mari05liM
 */
//public class TanderaTextView extends TextView {
public class TanderaTextView extends androidx.appcompat.widget.AppCompatTextView {

    @Inject
    FontManager mFontManager;

    public TanderaTextView(Context context) {
        this(context, null);
    }

    public TanderaTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TanderaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MoviesApp.from(context).inject(this);

        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TanderaTextView);
            setFont(a.getInt(R.styleable.TanderaTextView_tda_font, 7));
            a.recycle();
        }
    }

    public void setFont(final int font) {
        Typeface typeface = mFontManager.getFont(font);
        if (typeface != null) {
            setPaintFlags(getPaintFlags() | TextPaint.SUBPIXEL_TEXT_FLAG);
            setTypeface(typeface);
        }
    }

}