package com.mari05lim.tandera.views.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.views.activities.SettingsActivity;

import org.sufficientlysecure.donations.google.util.IabHelper;
import org.sufficientlysecure.donations.google.util.IabResult;
import org.sufficientlysecure.donations.google.util.Purchase;

public class DonateFragment extends Fragment {

    public static final String ARG_DEBUG = "debug";

    public static final String ARG_GOOGLE_ENABLED = "googleEnabled";
    public static final String ARG_GOOGLE_PUBKEY = "googlePubkey";
    public static final String ARG_GOOGLE_CATALOG = "googleCatalog";
    public static final String ARG_GOOGLE_CATALOG_VALUES = "googleCatalogValues";

    private static final String TAG = "Donations Library";

    private static final String[] CATALOG_DEBUG = new String[]{"android.test.purchased",
            "android.test.canceled", "android.test.refunded", "android.test.item_unavailable"};

    private Spinner mGoogleSpinner;

    private IabHelper mHelper;

    protected boolean mDebug = false;

    protected boolean mGoogleEnabled = false;
    protected String mGooglePubkey = "";
    protected String[] mGgoogleCatalog = new String[]{};
    protected String[] mGoogleCatalogValues = new String[]{};

    public static DonateFragment newInstance(boolean debug, boolean googleEnabled, String googlePubkey, String[] googleCatalog,
                                                String[] googleCatalogValues) {
        DonateFragment donationsFragment = new DonateFragment();
        Bundle args = new Bundle();

        args.putBoolean(ARG_DEBUG, debug);
        args.putBoolean(ARG_GOOGLE_ENABLED, googleEnabled);
        args.putString(ARG_GOOGLE_PUBKEY, googlePubkey);
        args.putStringArray(ARG_GOOGLE_CATALOG, googleCatalog);
        args.putStringArray(ARG_GOOGLE_CATALOG_VALUES, googleCatalogValues);

        donationsFragment.setArguments(args);
        return donationsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDebug = getArguments().getBoolean(ARG_DEBUG);
        mGoogleEnabled = getArguments().getBoolean(ARG_GOOGLE_ENABLED);
        mGooglePubkey = getArguments().getString(ARG_GOOGLE_PUBKEY);
        mGgoogleCatalog = getArguments().getStringArray(ARG_GOOGLE_CATALOG);
        mGoogleCatalogValues = getArguments().getStringArray(ARG_GOOGLE_CATALOG_VALUES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.donate_fragment, container, false);
    }

    @TargetApi(11)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            mGoogleSpinner = getActivity().findViewById(
                    R.id.donations_google_android_market_spinner);
            ArrayAdapter<CharSequence> adapter;
            if (mDebug) {
                adapter = new ArrayAdapter<CharSequence>(getActivity(),
                        android.R.layout.simple_spinner_item, CATALOG_DEBUG);
            } else {
                adapter = new ArrayAdapter<CharSequence>(getActivity(),
                        android.R.layout.simple_spinner_item, mGoogleCatalogValues);
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mGoogleSpinner.setAdapter(adapter);

            Button btGoogle = getActivity().findViewById(
                    R.id.donations_google_android_market_donate_button);
            btGoogle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        donateGoogleOnClick(v);
                    } catch (IllegalStateException e) {
                        if (mDebug)
                            Log.e(TAG, e.getMessage());

                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.donations_google_android_market_not_supported_title)
                                .content(R.string.donations_google_android_market_not_supported)
                                .positiveText(R.string.ok)
                                .iconRes(R.drawable.stop)
                                .limitIconToDefaultSize()
                                .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
                                .show();
                    }
                }
            });

            if (mDebug)
                Log.d(TAG, "Creating IAB helper.");
            mHelper = new IabHelper(getActivity(), mGooglePubkey);

            mHelper.enableDebugLogging(mDebug);

            if (mDebug)
                Log.d(TAG, "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (mDebug)
                        Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {

                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.donations_google_android_market_not_supported_title)
                                .content(R.string.donations_google_android_market_not_supported)
                                .positiveText(R.string.ok)
                                .iconRes(R.drawable.stop)
                                .limitIconToDefaultSize()
                                .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
                                .show();
                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;
                }
            });
    }

    public void donateGoogleOnClick(View view) {
        final int index;
        index = mGoogleSpinner.getSelectedItemPosition();
        if (mDebug)
            Log.d(TAG, "selected item in spinner: " + index);

        if (mDebug) {
            mHelper.launchPurchaseFlow(getActivity(),
                    CATALOG_DEBUG[index], IabHelper.ITEM_TYPE_INAPP,
                    0, mPurchaseFinishedListener, null);
        } else {
            mHelper.launchPurchaseFlow(getActivity(),
                    mGgoogleCatalog[index], IabHelper.ITEM_TYPE_INAPP,
                    0, mPurchaseFinishedListener, null);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mDebug)
                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isSuccess()) {
                if (mDebug)
                    Log.d(TAG, "Purchase successful.");

                // directly consume in-app purchase, so that people can donate multiple times
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.donations_thanks_dialog_title)
                        .content(R.string.donations_thanks_dialog)
                        .positiveText(R.string.ok)
                        .iconRes(R.drawable.love)
                        .limitIconToDefaultSize()
                        .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
                        .show();
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mDebug)
                Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            if (mHelper == null) return;

            if (result.isSuccess()) {
                if (mDebug)
                    Log.d(TAG, "Consumption successful. Provisioning.");
            }
            if (mDebug)
                Log.d(TAG, "End consumption flow.");
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mDebug)
            Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            if (mDebug)
                Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

}