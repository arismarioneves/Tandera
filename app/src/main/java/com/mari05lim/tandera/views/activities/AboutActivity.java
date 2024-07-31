package com.mari05lim.tandera.views.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.util.AboutUtils;
import com.mari05lim.tandera.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * DEV Mari05liM
 */
public class AboutActivity extends BaseNavigationActivity {

    public static final String KEY_LICENSES = "com.mari05lim.tandera.licenses";
    public static final String KEY_VERSION = "com.mari05lim.tandera.version";
    public static final String KEY_SHARE = "com.mari05lim.tandera.share";
    public static final String KEY_RATE = "com.mari05lim.tandera.rate";

    @Override
    protected void handleIntent(Intent intent, Display display) { }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_singlepane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        if (getDisplay() != null) {
            getDisplay().showUpNavigation(true);
            getDisplay().setActionBarTitle(this.getResources().getString(R.string.category_about));
        }

        if (savedInstanceState == null) {
            Fragment fragment = new AboutHeadersFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchToAbout(String aboutId) {
        Bundle args = new Bundle();
        args.putString("about", aboutId);
        Fragment f = new AboutFragment();
        f.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, f);
        ft.addToBackStack(null);
        ft.commit();
    }

    protected void setupAboutAbout(final Context context, Preference buildVersionPreference,
                                      Preference shareApp, Preference rateApp) {

        buildVersionPreference.setSummary(Utils.getVersion(context));

        shareApp.setOnPreferenceClickListener(preference -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getText(R.string.share_app_text) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName();
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, getText(R.string.share_app)));
            return true;
        });

        rateApp.setOnPreferenceClickListener(preference -> {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
            return true;
        });

    }

    protected void setupLegalAbout(final Context context, Preference licensesPreference){
        licensesPreference.setOnPreferenceClickListener(preference -> {
            AboutUtils.showOpenSourceLicensesDialog(context);
            return true;
        });
    }

    public static class AboutHeadersFragment extends Fragment {
        private AboutActivity.AboutHeadersFragment.HeaderAdapter adapter;
        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_about, container, false);
            listView = v.findViewById(R.id.listViewSettingsHeaders);

            return v;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            adapter = new AboutActivity.AboutHeadersFragment.HeaderAdapter(getActivity(), buildHeaders());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AboutActivity.AboutHeadersFragment.Header item = adapter.getItem(position);
                    ((AboutActivity) getActivity()).switchToAbout(item.aboutId);
                }
            });
        }

        private List<AboutActivity.AboutHeadersFragment.Header> buildHeaders() {
            List<AboutActivity.AboutHeadersFragment.Header> headers = new LinkedList<>();

            headers.add(new AboutActivity.AboutHeadersFragment.Header(R.string.prefs_category_about, "about"));
            headers.add(new AboutActivity.AboutHeadersFragment.Header(R.string.prefs_category_legal, "legal"));

            return headers;
        }

        private static class HeaderAdapter extends ArrayAdapter<AboutActivity.AboutHeadersFragment.Header> {
            private final LayoutInflater mInflater;

            private static class HeaderViewHolder {
                TextView title;

                public HeaderViewHolder(View view) {
                    title = view.findViewById(R.id.textViewSettingsHeader);
                }
            }

            public HeaderAdapter(Context context, List<AboutActivity.AboutHeadersFragment.Header> headers) {
                super(context, 0, headers);
                mInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                AboutActivity.AboutHeadersFragment.HeaderAdapter.HeaderViewHolder viewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_settings_header, parent, false);
                    viewHolder = new AboutActivity.AboutHeadersFragment.HeaderAdapter.HeaderViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (AboutActivity.AboutHeadersFragment.HeaderAdapter.HeaderViewHolder) convertView.getTag();
                }

                viewHolder.title.setText(getContext().getString(getItem(position).titleRes));

                return convertView;
            }
        }

        public static final class Header {
            public int titleRes;
            public String aboutId;

            public Header(int titleResId, String aboutId) {
                this.titleRes = titleResId;
                this.aboutId = aboutId;
            }
        }
    }

    public static class AboutFragment extends PreferenceFragment{

        private AboutActivity mActivity;

        @Override
        public void onAttach(Activity activity) {
            if (activity instanceof AboutActivity) {
                mActivity = (AboutActivity) activity;
            }
            super.onAttach(activity);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String about = getArguments().getString("about");
            assert about != null;
            switch (about) {
                case "about":
                    addPreferencesFromResource(R.xml.about_info);
                    mActivity.setupAboutAbout(getActivity(),
                            findPreference(KEY_VERSION),
                            findPreference(KEY_SHARE),
                            findPreference(KEY_RATE));
                    break;
                case "legal":
                    addPreferencesFromResource(R.xml.about_legal);
                    mActivity.setupLegalAbout(getActivity(), findPreference(KEY_LICENSES));
                    break;
            }
        }
    }
}