package com.mari05lim.tandera.views.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.util.FileLog;
import com.mari05lim.tandera.util.AnimUtils;
import com.mari05lim.tandera.util.CircularReveal;
import com.mari05lim.tandera.util.TanderaPreferences;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;

import java.util.List;

import static com.mari05lim.tandera.model.Constants.ENABLE_ADMOB_BANNER_ADS;
import static com.mari05lim.tandera.model.Constants.ENABLE_ADMOB_INTERSTITIAL_ADS;

/**
 * DEV Mari05liM
 */
public class TvActivity extends BaseNavigationActivity {

    private boolean mIsReturning;
    private Fragment currentFragment;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    int cont = 1;

    @Override
    protected int getContentViewLayoutId() {
        return ENABLE_ADMOB_BANNER_ADS ? R.layout.activity_no_drawer_ads : R.layout.activity_no_drawer;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && TanderaPreferences.areAnimationsEnabled(this)) {
                currentFragment = display.showTvDetailFragmentBySharedElement(intent.getStringExtra(Display.PARAM_ID));
            } else {
                display.showTvDetailFragment(intent.getStringExtra(Display.PARAM_ID));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && TanderaPreferences.areAnimationsEnabled(this)) {
            setupWindowAnimations();
            animateManually(true);
        } else {
            animateManually(false);
        }

        //AdMob
        MobileAds.initialize(this, initializationStatus -> {
        });

        if (ENABLE_ADMOB_BANNER_ADS) {
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        loadInterstitialAd();
    }

    private void loadInterstitialAd() {
        if (ENABLE_ADMOB_INTERSTITIAL_ADS) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_ad_unit_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        }
    }

    public void showInterstitialAd() {
        if (ENABLE_ADMOB_INTERSTITIAL_ADS) {
            if (mInterstitialAd.isLoaded()) {
                //if (cont == ADMOB_INTERSTITIAL_ADS_INTERVAL) {
                mInterstitialAd.show();
                //cont = 1;
                //} else {
                //cont++;
                //}
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentFragment = null;
    }

    @TargetApi(21)
    private void setupWindowAnimations() {
        postponeEnterTransition();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                FileLog.d("animations", "TvActivity: onSharedElementStart");
                if (!mIsReturning) {
                    getWindow().setEnterTransition(makeEnterTransition(getSharedElement(sharedElements)));
                }
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            }

            private View getSharedElement(List<View> sharedElements) {
                for (final View view : sharedElements) {
                    if (view instanceof ImageView) {
                        return view;
                    }
                }
                return null;
            }
        });
        // We are not interested in defining a new Enter Transition. Instead we change default transition duration
        getWindow().getEnterTransition().setDuration(getResources().getInteger(R.integer.anim_duration_medium));
    }

    @TargetApi(21)
    private void setupFinishAnimations() {
        getWindow().setReturnTransition(makeReturnTransition());
    }

    @TargetApi(21)
    private Transition makeEnterTransition(View sharedElement) {
        Preconditions.checkNotNull(currentFragment, "Current Fragment cannot be null");
        View rootView = currentFragment.getView();

        assert rootView != null;

        TransitionSet enterTransition = new TransitionSet();

        if (sharedElement != null) {
            // Play a circular reveal animation starting beneath the shared element.
            Transition circularReveal = new CircularReveal(sharedElement);
            circularReveal.addTarget(rootView.findViewById(R.id.data_container));
            enterTransition.addTransition(circularReveal);
        }

        // Slide the cards in through the bottom of the screen.
        Transition cardSlide = new Slide(Gravity.BOTTOM);
        cardSlide.addTarget(rootView.findViewById(R.id.primary_recycler_view));
        enterTransition.addTransition(cardSlide);

        // Don't fade the navigation/status bars.
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.addTransition(fade);

        final Resources res = getResources();
        final TanderaImageView backgroundImage = rootView.findViewById(R.id.fanart_image);
        backgroundImage.setAlpha(0f);
        final FloatingActionButton fButton = rootView.findViewById(R.id.button_fab);
        if (fButton != null) {
            fButton.setAlpha(0f);
        }
        enterTransition.addListener(new AnimUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
            backgroundImage.animate().alpha(1f).setDuration(res.getInteger(R.integer.image_background_fade_millis));

                if (fButton != null) {
                    // we rely on the window enter content transition to show the fab. This isn't run on
                    // orientation changes so manually show it.
                    Animator showFab = ObjectAnimator.ofPropertyValuesHolder(fButton,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f));
                    showFab.setStartDelay(300L);
                    showFab.setDuration(300L);
                    showFab.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(TvActivity.this));
                    showFab.start();

                    showInterstitialAd();
                }

            }
        });
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        return enterTransition;
    }

    @TargetApi(21)
    private Transition makeReturnTransition() {
        Preconditions.checkNotNull(currentFragment, "Current Fragment cannot be null");
        View rootView = currentFragment.getView();
        assert rootView != null;

        final FloatingActionButton fButton = rootView.findViewById(R.id.button_fab);
        if (fButton != null) {
            fButton.setAlpha(0f);
        }

        TransitionSet returnTransition = new TransitionSet();

        // Slide and fade the circular reveal container off the top of the screen.
        TransitionSet slideFade = new TransitionSet();
        slideFade.addTarget(rootView.findViewById(R.id.data_container));
        slideFade.addTransition(new Slide(Gravity.TOP));
        slideFade.addTransition(new Fade());
        returnTransition.addTransition(slideFade);

        // Slide the cards off the bottom of the screen.
        Transition cardSlide = new Slide(Gravity.BOTTOM);
        cardSlide.addTarget(rootView.findViewById(R.id.primary_recycler_view));
        returnTransition.addTransition(cardSlide);

        returnTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        return returnTransition;
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        if (currentFragment == null || !TanderaPreferences.areAnimationsEnabled(this)) {
            animateManually(false);
            finish();
        } else {
            //setupFinishAnimations();
            super.finishAfterTransition();
        }
    }

}