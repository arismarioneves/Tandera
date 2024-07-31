package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class MovieWatchedToggler extends LinearLayout {

    private final int color;
    final TextView mContent;
    final View mDivider;
    private final ImageView mToggler;
    private final Drawable toggleOFF;
    private final Drawable toggleON;

    public MovieWatchedToggler(Context context) {
        this(context, null);
    }

    public MovieWatchedToggler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieWatchedToggler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.color = getResources().getColor(R.color.primary_dark);
        LayoutInflater.from(context).inflate(R.layout.item_watched_toggler, this, true);

        mContent = findViewById(R.id.textview_content);
        mDivider =  findViewById(R.id.divider);
        mToggler = findViewById(R.id.imageview_toggler);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieWatchedToggler);
        this.toggleOFF = a.getDrawable(R.styleable.MovieWatchedToggler_togglerOff);
        this.toggleON = a.getDrawable(R.styleable.MovieWatchedToggler_togglerOn);
        a.recycle();
    }

    public void setStatus(boolean parameter) {
        if (parameter) {
            showContent();
        } else {
            hideContent();
        }
    }

    private void hideContent() {
        this.mToggler.setImageDrawable(this.toggleOFF);
        this.mToggler.setBackgroundColor(Color.TRANSPARENT);
    }

    private void showContent() {
        this.mToggler.setImageDrawable(this.toggleON);
        this.mToggler.setBackgroundColor(this.color);
    }

}