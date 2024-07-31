package com.mari05lim.tandera.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class AboutUtils {

    public static void showOpenSourceLicensesDialog(Context context) {
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_asset/licenses.xhtml");

        new MaterialDialog.Builder(context)
                .title(R.string.about_licenses_title)
                .customView(webView, false)
                .theme(Theme.LIGHT)
                .show();
    }

    public static class OpenSourceLicensesDialog extends DialogFragment {
        public OpenSourceLicensesDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/licenses.xhtml");

            return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.about_licenses_title)
                .setView(webView)
                .setPositiveButton(R.string.ok,
                        (dialog, whichButton) -> dialog.dismiss()
                )
                .create();
        }
    }

}