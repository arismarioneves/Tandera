package com.mari05lim.tandera.views.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;

import static com.mari05lim.tandera.model.Constants.ENABLE_ADMOB_BANNER_ADS;

/**
 * DEV Mari05liM
 */
public class PersonActivity extends BaseNavigationActivity {

    private AdView mAdView;

    @Override
    protected int getContentViewLayoutId() {
        return ENABLE_ADMOB_BANNER_ADS ? R.layout.activity_no_drawer_ads : R.layout.activity_no_drawer;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            display.showPersonFragment(intent.getStringExtra(Display.PARAM_ID));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AdMob
        MobileAds.initialize(this, initializationStatus -> {
        });

        if (ENABLE_ADMOB_BANNER_ADS) {
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }
    }

}