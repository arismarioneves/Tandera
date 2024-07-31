package com.mari05lim.tandera.views.listeners;

import com.google.android.material.appbar.AppBarLayout;

import com.mari05lim.tandera.model.util.FileLog;

/**
 * DEV Mari05liM
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "AppBarStateChange";

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.EXPANDED;
    private int mInitialPosition = 0;
    private boolean isAnimating;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
            mInitialPosition = 0;
            FileLog.d(TAG, "onOffsetChanged to EXPANDED");
            isAnimating = false;
            appBarLayout.setEnabled(true);
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
            mInitialPosition = appBarLayout.getTotalScrollRange();
            FileLog.d(TAG, "onOffsetChanged to COLLAPSED");
            isAnimating = false;
            appBarLayout.setEnabled(true);
        } else {
            FileLog.d(TAG, "onOffsetChanged to IDLE");
            int diff = Math.abs(Math.abs(i) - mInitialPosition);
            if (diff >= appBarLayout.getTotalScrollRange() / 3 && !isAnimating) {
                FileLog.d(TAG, "onOffsetChanged 4");
                isAnimating = true;
                appBarLayout.setEnabled(false);
            }
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

}