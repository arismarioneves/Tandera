package com.mari05lim.tandera.views.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.util.StringFetcher;
import com.mari05lim.tandera.mvp.presenters.SettingsPresenter;
import com.mari05lim.tandera.mvp.views.SettingsView;
import com.mari05lim.tandera.util.TanderaPreferences;
import com.mari05lim.tandera.util.Utils;
import com.mari05lim.tandera.views.fragments.MovieDetailFragment;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * DEV Mari05liM
 */
public class SettingsActivity extends BaseNavigationActivity implements SettingsView {

    @InjectPresenter
    SettingsPresenter mPresenter;

    @Inject
    StringFetcher mStringFetcher;

    private static final String KEY_CLEAR_CACHE = "com.mari05lim.tandera.clear_cache";
    private static final String KEY_CLEAR_DATABASE = "com.mari05lim.tandera.local_database";
    public static final String KEY_NOTIFICATION = "com.mari05lim.tandera.notification_settings";

    public static @StyleRes
    int THEME;

    public static void setTheme(Context context) {
        int theme = TanderaPreferences.getApplicationTheme(context);
        switch (theme) {
            case 0:
                THEME = R.style.Theme_Tandera_Claro;
                break;
            case 1:
                THEME = R.style.Theme_Tandera_Dark;
                break;
        }
    }

    public static boolean hasTheme() {
        return THEME != 0;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) { }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_singlepane;
    }

    @Override
    public void onWatchedCleared() {
        Toast.makeText(this, getResources().getString(R.string.action_cleared_watched), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MoviesApp.from(this).inject(this);

        setupActionBar();
        if (getDisplay() != null) {
            getDisplay().showUpNavigation(true);
            getDisplay().setActionBarTitle(MoviesApp.get().getStringFetcher().getString(R.string.settings_title));
        }

        if (savedInstanceState == null) {
            Fragment fragment = new SettingsHeadersFragment();
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

    public void switchToSettings(String settingsId) {
        Bundle args = new Bundle();
        args.putString("settings", settingsId);
        Fragment f = new SettingsFragment();
        f.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, f);
        ft.addToBackStack(null);
        ft.commit();
    }

    protected void setupIterfaceSettings(final Activity activity, final Intent intent, Preference onlyWiFiPreference,
                                         Preference animationsPreference, Preference themePreference,
                                         Preference countryPreference, /*Preference languagePreference, */Preference notificationPreference) {

        themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (TanderaPreferences.KEY_THEME.equals(preference.getKey())) {
                Utils.updateTheme(activity.getApplicationContext(), (String) newValue);

                TaskStackBuilder.create(activity)
                        .addNextIntent(new Intent(activity, WatchlistActivity.class))
                        .addNextIntent(intent)
                        .startActivities();
            }
            return true;
        });

        countryPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (TanderaPreferences.KEY_COUNTRY.equals(preference.getKey())) {

                new MaterialDialog.Builder(SettingsActivity.this)
                        .title(R.string.pref_country_title)
                        .content(R.string.pref_country_alert)
                        .positiveText(R.string.ok)
                        .show();
            }
                return true;
        });

        /*languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (TanderaPreferences.KEY_LANGUAGE.equals(preference.getKey())) {

                String mLanguage = "";

                int language = TanderaPreferences.getApplicationLanguage(activity.getApplicationContext());
                switch (language) {
                    case 0:
                        mLanguage = Locale.getDefault().getLanguage();
                        break;
                    case 1:
                        mLanguage = mLanguageCode_PtBR;
                        break;
                    case 2:
                        mLanguage = mLanguageCode_EnUS;
                        break;
                }

                Resources resources = this.getResources();
                Locale locale = new Locale(mLanguage);
                Locale.setDefault(locale);
                android.content.res.Configuration config = new
                        android.content.res.Configuration();
                config.locale = locale;
                resources.updateConfiguration(config, resources.getDisplayMetrics());
                //restart base activity
                activity.finish();
                activity.startActivity(activity.getIntent());
            }
            return true;
        });*/

        notificationPreference.setOnPreferenceClickListener(preference -> {
            if (KEY_NOTIFICATION.equals(preference.getKey())) {

                Intent notification = new Intent();

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

                    notification.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    notification.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    notification.addFlags(FLAG_ACTIVITY_NEW_TASK);

                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {

                    notification.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    notification.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    notification.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    notification.putExtra("app_package", getPackageName());
                    notification.putExtra("app_uid", getApplicationInfo().uid);

                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                    notification.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    notification.addCategory(Intent.CATEGORY_DEFAULT);
                    notification.setData(Uri.parse("package:" + getPackageName()));

                } else {
                    Toast.makeText(getApplicationContext(), R.string.android_old, Toast.LENGTH_LONG).show();
                }

                startActivity(notification);
            }
            return true;
        });

        setListPreferenceSummary((ListPreference) themePreference);
        setListPreferenceSummary((ListPreference) countryPreference);
        //setListPreferenceSummary((ListPreference) languagePreference);

        ((CheckBoxPreference) onlyWiFiPreference).setChecked(TanderaPreferences.isLargeDataOverWifiOnly(activity.getApplicationContext()));
        ((CheckBoxPreference) animationsPreference).setChecked(TanderaPreferences.areAnimationsEnabled(activity.getApplicationContext()));
    }

    protected void setupCacheSettings(final Context context, Preference clearCachePreference, Preference clearDatabasePreference) {
        clearCachePreference.setOnPreferenceClickListener(preference -> {
            Display display = getDisplay();
            if (display != null) {
                // try to open app info where user can clear app cache folders
                Intent intent = new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                if (!display.tryStartActivity(intent, false)) {
                    // try to open all apps view if detail view not available
                    intent = new Intent(
                            Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    display.tryStartActivity(intent, true);
                }
            }
            return true;
        });

        clearDatabasePreference.setOnPreferenceClickListener(preference -> {
            if (mPresenter != null) {
                new MaterialDialog.Builder(SettingsActivity.this)
                        .title(R.string.local_database_title)
                        .content(R.string.local_database_alert)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> mPresenter.clearWatched(SettingsActivity.this))
                        .show();
            }
            return true;
        });
    }

    public static void setListPreferenceSummary(ListPreference listPref) {
        // Set summary to be the user-description for the selected value
        listPref.setSummary(listPref.getEntry().toString().replaceAll("%", "%%"));
    }

    public static class SettingsHeadersFragment extends Fragment {
        private HeaderAdapter adapter;
        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_settings, container, false);
            listView = v.findViewById(R.id.listViewSettingsHeaders);

            return v;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            adapter = new HeaderAdapter(getActivity(), buildHeaders());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Header item = adapter.getItem(position);
                ((SettingsActivity) getActivity()).switchToSettings(item.settingsId);
            });
        }

        private List<Header> buildHeaders() {
            List<Header> headers = new LinkedList<>();

            headers.add(new Header(R.string.prefs_category_basic, "interface"));
            headers.add(new Header(R.string.prefs_category_cache, "cache"));

            return headers;
        }

        private static class HeaderAdapter extends ArrayAdapter<Header> {
            private final LayoutInflater mInflater;

            private static class HeaderViewHolder {
                TextView title;

                public HeaderViewHolder(View view) {
                    title = view.findViewById(R.id.textViewSettingsHeader);
                }
            }

            public HeaderAdapter(Context context, List<Header> headers) {
                super(context, 0, headers);
                mInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                HeaderViewHolder viewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_settings_header, parent, false);
                    viewHolder = new HeaderViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (HeaderViewHolder) convertView.getTag();
                }

                viewHolder.title.setText(getContext().getString(getItem(position).titleRes));

                return convertView;
            }
        }

        public static final class Header {
            public int titleRes;
            public String settingsId;

            public Header(int titleResId, String settingsId) {
                this.titleRes = titleResId;
                this.settingsId = settingsId;
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SettingsActivity mActivity;

        @Override
        public void onAttach(Activity activity) {
            if (activity instanceof SettingsActivity) {
                mActivity = (SettingsActivity) activity;
            }
            super.onAttach(activity);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String settings = getArguments().getString("settings");
            assert settings != null;
            switch (settings) {
                case "interface":
                    addPreferencesFromResource(R.xml.settings_interface);
                    mActivity.setupIterfaceSettings(getActivity(), getActivity().getIntent(),
                            findPreference(TanderaPreferences.KEY_ONLYWIFI),
                            findPreference(TanderaPreferences.KEY_ANIMATIONS),
                            findPreference(TanderaPreferences.KEY_THEME),
                            findPreference(TanderaPreferences.KEY_COUNTRY),
                            //findPreference(TanderaPreferences.KEY_LANGUAGE),
                            findPreference(KEY_NOTIFICATION));
                    break;
                case "cache":
                    addPreferencesFromResource(R.xml.settings_cache);
                    mActivity.setupCacheSettings(getActivity(),
                            findPreference(KEY_CLEAR_CACHE),
                            findPreference(KEY_CLEAR_DATABASE));
                    break;
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);

            if (preference != null) {
            }

            if (TanderaPreferences.KEY_THEME.equals(key)) {
                if (preference != null) {
                    setListPreferenceSummary((ListPreference) preference);
                }
            }

            if (TanderaPreferences.KEY_COUNTRY.equals(key)) {
                if (preference != null) {
                    setListPreferenceSummary((ListPreference) preference);
                }
            }

            /*if (TanderaPreferences.KEY_LANGUAGE.equals(key)) {
                if (preference != null) {
                    setListPreferenceSummary((ListPreference) preference);
                }
            }*/

            if (TanderaPreferences.KEY_ONLYWIFI.equals(key)) {
                if (preference != null) {
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                    TanderaPreferences.setLargeDataOverWifiOnly(getActivity().getApplicationContext(), checkBoxPreference.isChecked());
                }
            }

            if (TanderaPreferences.KEY_ANIMATIONS.equals(key)) {
                if (preference != null) {
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                    TanderaPreferences.setAnimationsEnabled(getActivity().getApplicationContext(), checkBoxPreference.isChecked());
                }
            }
        }
    }

}