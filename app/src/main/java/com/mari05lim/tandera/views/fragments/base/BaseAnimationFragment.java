package com.mari05lim.tandera.views.fragments.base;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.widget.FrameLayout;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.util.TanderaPreferences;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;

import java.io.Serializable;

import butterknife.BindView;
import io.codetail.animation.SupportAnimator;

/**
 * DEV Mari05liM
 */
public abstract class BaseAnimationFragment<M extends Serializable> extends BaseDetailFragment<M> {

    protected static final String KEY_IMAGE_POSITION = "_position";

    @Nullable @BindView(R.id.animation_layout)
    FrameLayout mAnimationLayout;

    @Nullable @BindView(R.id.button_fab)
    protected FloatingActionButton mFloatingButton;

    @Nullable @BindView(R.id.animation_container)
    protected FrameLayout mAnimationContainer;

    @Nullable @BindView(R.id.data_container)
    protected View mDataContainer;

    @Nullable @BindView(R.id.poster_image)
    protected TanderaImageView mPosterImageView;

    protected static final int SCALE_DELAY = 30;

    protected boolean hasAnimationContainer() {
        return mAnimationContainer != null;
    }

    protected boolean hasFAB() {
        return mFloatingButton != null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if (hasAnimationContainer()) {
            mAnimationContainer.setVisibility(View.GONE);
            mDataContainer.setVisibility(View.VISIBLE);
        }

        initiaizeStartAnimation();

        if (hasFAB() && hasAnimationContainer()) {
            mFloatingButton.setVisibility(View.VISIBLE);
            mFloatingButton.setOnClickListener(mOnClickListener);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    final private View.OnClickListener mOnClickListener = v -> onFabClicked();

    public abstract void onFabClicked();

    private void initiaizeStartAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && TanderaPreferences.areAnimationsEnabled(getContext())) {
            configureEnterTransition();
        }
    }

    protected abstract void configureEnterTransition();

    public static class SimpleAnimationListener implements SupportAnimator.AnimatorListener, ObjectAnimator.AnimatorListener{

        @Override
        public void onAnimationStart() {
        }

        @Override
        public void onAnimationEnd() {
        }

        @Override
        public void onAnimationCancel() {
        }

        @Override
        public void onAnimationRepeat() {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

}