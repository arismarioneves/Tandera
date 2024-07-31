package com.mari05lim.tandera.views.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mari05lim.tandera.BuildConfig;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.views.fragments.DonateFragment;

public class DonateActivity extends BaseNavigationActivity {

    @Override
    protected void handleIntent(Intent intent, Display display) { }

    private static final String GOOGLE_PUBKEY = "";
    private static final String[] GOOGLE_CATALOG = new String[]{
            "tandera.coffee.3",
            "tandera.coffee.5",
            "tandera.coffee.8",
            "tandera.coffee.10"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.donate_activity);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DonateFragment donationsFragment;

        donationsFragment = DonateFragment.newInstance(BuildConfig.DEBUG, true, GOOGLE_PUBKEY, GOOGLE_CATALOG,
                getResources().getStringArray(R.array.donate_google_catalog_values));

        ft.replace(R.id.donate_activity_container, donationsFragment, "donateFragment");
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("donateFragment");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}