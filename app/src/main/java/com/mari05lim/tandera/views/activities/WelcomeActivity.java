package com.mari05lim.tandera.views.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.views.custom_views.CirclePageIndicator;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.util.VisitManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * DEV Mari05liM
 */
public class WelcomeActivity extends BaseActivity {

    @Inject VisitManager mVisitManager;

    private static final int WELCOME_ACTIVITY_PAGER_SIZE = 3;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.welcome_view_1)
    View mWelcomeViewPage1;

    @BindView(R.id.welcome_view_2)
    View mWelcomeViewPage2;

    @BindView(R.id.welcome_view_3)
    View mWelcomeViewPage3;

    @BindView(R.id.welcome_pager_dot_indicator)
    CirclePageIndicator mPageIndicator;

    @Override
    protected int getThemeResId() {
        return R.style.Theme_Tandera_FirstLauncher;
    }

    private void finishIfFirstVisitAlreadyPerformed() {
        if (mVisitManager.isFirstVisitPerformed()) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MoviesApp.from(this).inject(this);
        super.onCreate(savedInstanceState);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WelcomePagerAdapter welcomePagerAdapter = new WelcomePagerAdapter();
        this.mViewPager.setAdapter(welcomePagerAdapter);
        this.mViewPager.setOffscreenPageLimit(WELCOME_ACTIVITY_PAGER_SIZE);
        this.mPageIndicator.setViewPager(this.mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finishIfFirstVisitAlreadyPerformed();
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (mVisitManager.isFirstVisitPerformed()) {
            if (display != null) {
                display.startWatchlistActivity();
            }
        }
    }

    @OnClick({R.id.continue_btn})
    public void onContinueClicked() {
        mVisitManager.recordFirstVisitPerformed();
        if (getDisplay() != null) {
            getDisplay().startWatchlistActivity();
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_welcome;
    }

    private class WelcomePagerAdapter extends PagerAdapter {

        public WelcomePagerAdapter() {
        }

        @Override
        public int getCount() {
            return WELCOME_ACTIVITY_PAGER_SIZE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position) {
                default:
                    return null;
                case 0:
                    return WelcomeActivity.this.mWelcomeViewPage1;
                case 1:
                    return WelcomeActivity.this.mWelcomeViewPage2;
                case 2:
                    return WelcomeActivity.this.mWelcomeViewPage3;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}