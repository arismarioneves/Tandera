package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class ExpandableTextView extends TanderaTextView implements View.OnClickListener {

    private final int mCollapsedMaxLines;
    private boolean mExpanded;

    private View.OnClickListener mDelegateClickListener;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, 0 , defStyle);
        mCollapsedMaxLines = a.getInt(R.styleable.ExpandableTextView_collapsed_maxLines, 8);
        mExpanded = a.getBoolean(R.styleable.ExpandableTextView_expanded_status, false);
        a.recycle();

        if (mExpanded) {
            expand();
        } else {
            collapse();
        }

        super.setOnClickListener(this);
    }

    public void expand() {
        setMaxLines(Integer.MAX_VALUE);
        mExpanded = true;
    }

    public void collapse() {
        setMaxLines(mCollapsedMaxLines);
        mExpanded = false;
    }

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        mDelegateClickListener = l;
    }

    @Override
    public final void onClick(View v) {
        if (mExpanded) {
            collapse();
        } else {
            expand();
        }

        if (mDelegateClickListener != null) {
            mDelegateClickListener.onClick(v);
        }
    }

}