package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.util.FontManager;

import javax.inject.Inject;

/**
 * DEV Mari05liM
 */
public class TanderaEditText extends AppCompatEditText {

    @Inject
    FontManager mTypefaceManager;

    public TanderaEditText(Context context) {
        this(context, null);
    }

    public TanderaEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TanderaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MoviesApp.from(context).inject(this);

        if (isInEditMode())
            return;
        setFont();
    }

    public void setFont() {
        Typeface typeface = mTypefaceManager.getRobotoCondensedLight();
        if (typeface != null) {
            setPaintFlags(getPaintFlags() | TextPaint.SUBPIXEL_TEXT_FLAG);
            setTypeface(typeface);
        }
    }

}