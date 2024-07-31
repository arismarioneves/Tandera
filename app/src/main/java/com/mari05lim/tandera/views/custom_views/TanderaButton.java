package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatButton;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.util.FontManager;

import javax.inject.Inject;

/**
 * DEV Mari05liM
 */
public class TanderaButton extends AppCompatButton {

    @Inject
    FontManager mTypefaceManager;

    public TanderaButton(Context context) {
        this(context, null);
    }

    public TanderaButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TanderaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MoviesApp.from(context).inject(mTypefaceManager);

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