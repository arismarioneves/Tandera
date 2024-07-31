package com.mari05lim.tandera;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.util.TanderaPreferences;
import com.mari05lim.tandera.views.activities.AboutActivity;
import com.mari05lim.tandera.views.activities.DonateActivity;
import com.mari05lim.tandera.views.activities.StreamActivity;
import com.mari05lim.tandera.views.activities.SearchAPIActivity;
import com.mari05lim.tandera.util.ColorUtils;
import com.mari05lim.tandera.util.TmdbUtils;
import com.mari05lim.tandera.util.Utils;
import com.mari05lim.tandera.views.activities.MovieActivity;
import com.mari05lim.tandera.views.activities.PersonActivity;
import com.mari05lim.tandera.views.activities.SearchItemsActivity;
import com.mari05lim.tandera.views.activities.SettingsActivity;
import com.mari05lim.tandera.views.activities.TvActivity;
import com.mari05lim.tandera.views.activities.WatchlistActivity;
import com.mari05lim.tandera.views.fragments.MovieDetailFragment;
import com.mari05lim.tandera.views.fragments.MoviesTabFragment;
import com.mari05lim.tandera.views.fragments.PersonDetailFragment;
import com.mari05lim.tandera.views.fragments.RelatedMoviesFragment;
import com.mari05lim.tandera.views.fragments.SearchFragment;
import com.mari05lim.tandera.views.fragments.SearchMoviesListFragment;
import com.mari05lim.tandera.views.fragments.SearchPeopleListFragment;
import com.mari05lim.tandera.views.fragments.SearchShowsListFragment;
import com.mari05lim.tandera.views.fragments.TvShowDetailFragment;
import com.mari05lim.tandera.views.fragments.TvShowsTabFragment;
import com.mari05lim.tandera.views.fragments.WatchedFragment;
import com.mari05lim.tandera.model.Constants;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;

/**
 * DEV Mari05liM
 */
public class MoviesDisplay implements Display {

    private static final TypedValue sTypedValue = new TypedValue();

    private final int mColorPrimaryDark;

    private final AppCompatActivity mActivity;
    private DrawerLayout mDrawerLayout;

    private Toolbar mToolbar;
    private boolean mCanChangeToolbarBackground;

    public MoviesDisplay(AppCompatActivity mActivity) {
        this.mActivity = Preconditions.checkNotNull(mActivity, "Activity can not be null");

        mActivity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, sTypedValue, true);
        mColorPrimaryDark = sTypedValue.data;
    }

    @Override
    public boolean hasSearchFragment() {
        Fragment f = mActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        return f instanceof SearchFragment;
    }

    @Override
    public void setDrawerLayout(DrawerLayout layout) {
        this.mDrawerLayout = layout;
    }

    private void showFragment(Fragment fragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showFragmentFromDrawer(Fragment fragment) {
        popEntireFragmentBackStack();

        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit();
    }

    private void startActivity(Intent intent, Bundle options) {
        ActivityCompat.startActivity(mActivity, intent, options);
    }

    @Override
    public void showWatched() {
        showFragmentFromDrawer(new WatchedFragment());
    }

    @Override
    public void showMovies() {
        showFragmentFromDrawer(new MoviesTabFragment());
    }

    @Override
    public void showTvShows() {
        showFragmentFromDrawer(new TvShowsTabFragment());
    }

    @Override
    public void showStream() {
        startStreamActivity();
    }

    @Override
    public void showSettings() {
        startSettingsActivity();
    }

    @Override
    public void showAbout() {
        startAboutActivity();
    }

    @Override
    public void startSearchListActivity(String listType, Bundle bundle) {
        Intent intent = new Intent(mActivity, SearchItemsActivity.class);
        intent.putExtra(PARAM_ID, listType);
        startActivity(intent, bundle);
        // mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void startWatchlistActivity() {
        Intent intent = new Intent(mActivity, WatchlistActivity.class);
        startActivity(intent, null);
    }

    @Override
    public void startStreamActivity() {
        Intent intent = new Intent(mActivity, StreamActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void startSettingsActivity() {
        Intent intent = new Intent(mActivity, SettingsActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void startAboutActivity() {
        Intent intent = new Intent(mActivity, AboutActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public Fragment showMovieDetailFragmentBySharedElements(String movieId) {
        Fragment fragment = MovieDetailFragment.newInstance(movieId);
        showFragmentFromDrawer(fragment);
        return fragment;
    }

    @Override
    public void showMovieDetailFragment(String movieId) {
        showFragmentFromDrawer(MovieDetailFragment.newInstance(movieId));
    }

    @TargetApi(21)
    @Override
    public void startMovieDetailActivityBySharedElements(String movieId, View view, String imagePosition) {
        ActivityOptions options
                = ActivityOptions.makeSceneTransitionAnimation(mActivity,
                        Pair.create(view, mActivity.getString(R.string.transition_poster)),
                        Pair.create(view, mActivity.getString(R.string.transition_poster_background)));

        Intent intent = new Intent(mActivity, MovieActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void startMovieDetailActivity(String movieId, Bundle bundle) {
        Intent intent = new Intent(mActivity, MovieActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        startActivity(intent, bundle);
    }

    @TargetApi(21)
    @Override
    public void startTvDetailActivityBySharedElements(String tvId, View view, String imagePosition) {
        ActivityOptions options
                = ActivityOptions.makeSceneTransitionAnimation(mActivity,
                        Pair.create(view, mActivity.getString(R.string.transition_poster)),
                        Pair.create(view, mActivity.getString(R.string.transition_poster_background)));

        Intent intent = new Intent(mActivity, TvActivity.class);
        intent.putExtra(PARAM_ID, tvId);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void startTvDetailActivity(String showId, Bundle bundle) {
        Intent intent = new Intent(mActivity, TvActivity.class);
        intent.putExtra(PARAM_ID, showId);
        startActivity(intent, bundle);
    }

    @Override
    public void showTvDetailFragment(String movieId) {
        showFragmentFromDrawer(TvShowDetailFragment.newInstance(movieId));
    }

    @Override
    public Fragment showTvDetailFragmentBySharedElement(String tvId) {
        Fragment fragment = TvShowDetailFragment.newInstance(tvId);
        showFragmentFromDrawer(fragment);
        return fragment;
    }

    @Override
    public void showRelatedMovies(String movieId) {
        showFragment(RelatedMoviesFragment.newInstance(movieId));
    }

    @Override
    public void startPersonDetailActivity(String id, Bundle bundle) {
        Intent intent = new Intent(mActivity, PersonActivity.class);
        intent.putExtra(PARAM_ID, id);
        startActivity(intent, bundle);
    }

    @TargetApi(21)
    @Override
    public void startPersonDetailActivityBySharedElement(String id, View view) {
        ActivityOptions options
                = ActivityOptions.makeSceneTransitionAnimation(mActivity,
                        Pair.create(view, mActivity.getString(R.string.transition_poster)),
                        Pair.create(view, mActivity.getString(R.string.transition_poster_background)));

        Intent intent = new Intent(mActivity, PersonActivity.class);
        intent.putExtra(PARAM_ID, id);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void showPersonDetailFragment(String id) {
        showFragmentFromDrawer(PersonDetailFragment.newInstance(id));
    }

    @Override
    public Fragment showPersonFragment(String tvId) {
        Fragment fragment = PersonDetailFragment.newInstance(tvId);
        showFragmentFromDrawer(fragment);
        return fragment;
    }

    @Override
    public boolean hasMainFragment() {
        return mActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_main) != null;
    }

    @Override
    public void setActionBarTitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }

    @Override
    public void setActionBarSubtitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(title);
        }
    }

    @Override
    public boolean popEntireFragmentBackStack() {
        final FragmentManager fm = mActivity.getSupportFragmentManager();
        final int backStackCount = fm.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fm.popBackStack();
        }
        return backStackCount > 0;
    }

    @Override
    public void finishActivity() {
        mActivity.finish();
    }

    @Override
    public void playYoutubeVideo(String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + id));

        mActivity.startActivity(intent);
    }

    @Override
    public void shareMovie(int movieId, String movieTitle) {
        String message = mActivity.getString(R.string.share_checkout, movieTitle) + "\n"
                + TmdbUtils.buildMovieUrl(movieId).replace("themoviedb", "tmdb") + "\n"
                + "https://play.google.com/store/apps/details?id=" + mActivity.getPackageName();
        startShareIntentChooser(message, R.string.share_movie);
    }

    @Override
    public void shareTvShow(int showId, String showTitle) {
        String message = mActivity.getString(R.string.share_checkout, showTitle) + "\n"
                + TmdbUtils.buildTvShowUrl(showId).replace("themoviedb", "tmdb") + "\n"
                + "https://play.google.com/store/apps/details?id=" + mActivity.getPackageName();
        startShareIntentChooser(message, R.string.share_tv_show);
    }

    @Override
    public void startShareIntentChooser(String message, @StringRes int titleResId) {
        ShareCompat.IntentBuilder ib = ShareCompat.IntentBuilder.from(mActivity);
        ib.setText(message);
        ib.setChooserTitle(titleResId);
        ib.setType("text/plain");
        try {
            ib.startChooser();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mActivity, R.string.app_not_available, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void openTmdbMovie(MovieWrapper movie) {
        openTmdbUrl(TmdbUtils.buildMovieUrl(movie.getTmdbId()));
    }

    @Override
    public void openTmdbPerson(PersonWrapper person) {
        openTmdbUrl(TmdbUtils.buildPersonUrl(person.getTmdbId()));
    }

    @Override
    public void openTmdbTvShow(ShowWrapper show) {
        openTmdbUrl(TmdbUtils.buildTvShowUrl(show.getTmdbId()));
    }

    @Override
    public void openTmdbUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        tryStartActivity(intent, true);
    }

    @Override
    public boolean tryStartActivity(Intent intent, boolean displayError) {
        boolean handled = false;

        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            try {
                mActivity.startActivity(intent);
                handled = true;
            } catch (ActivityNotFoundException exception) {
                Toast.makeText(mActivity, R.string.error_unknown, Toast.LENGTH_LONG).show();
            }
        }

        if (displayError && !handled) {
            Toast.makeText(mActivity, R.string.app_not_available, Toast.LENGTH_LONG).show();
        }
        return handled;
    }

    @Override
    public void performWebSearch(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        tryStartActivity(intent, true);
    }

    @Override
    public void showSearchFragment() {
        mActivity.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_main, SearchFragment.newInstance())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
    }

    @Override
    public void showSearchMoviesFragment() {
        showFragmentFromDrawer(new SearchMoviesListFragment());
    }

    @Override
    public void showSearchPeopleFragment() {
        showFragmentFromDrawer(new SearchPeopleListFragment());
    }

    @Override
    public void showSearchTvShowsFragment() {
        showFragmentFromDrawer(new SearchShowsListFragment());
    }

    @Override
    public void setStatusBarColor(float scroll) {
        final int statusBarColor = ColorUtils.blendColors(mColorPrimaryDark, 0, scroll);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(statusBarColor);
        } else if (Build.VERSION.SDK_INT >= 21) {
            mActivity.getWindow().setStatusBarColor(statusBarColor);
        }
    }

    @Override
    public void showUpNavigation(boolean show) {
        final ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setHomeAsUpIndicator(show ? R.drawable.ic_action_arrow_back : R.drawable.ic_menu);
        }
    }

    @Override
    public void setSupportActionBar(Object toolbar, boolean handleBackground) {
        mToolbar = (Toolbar) toolbar;
        mCanChangeToolbarBackground = handleBackground;
        if (mDrawerLayout != null && mToolbar != null) {
            final ActionBar ab = mActivity.getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setHomeButtonEnabled(true);
            }
        }
        setStatusBarColor(1f);
    }

    void setToolbarBackground(int color) {
        if (mCanChangeToolbarBackground && mToolbar != null) {
            mToolbar.setBackgroundColor(color);
        }
    }

    @Override
    public void closeDrawerLayout() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public boolean toggleDrawer() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isTablet() {
        return mActivity.getResources().getBoolean(R.bool.tablet);
    }

    @Override
    public void sendEmail() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        //intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{
            Constants.SUPPORT_MAIL
        });
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Tandera " + Utils.getVersion(mActivity) + " Feedback");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");

        Intent chooser = Intent.createChooser(intent, mActivity.getResources().getString(R.string.feedback));
        tryStartActivity(chooser, true);
    }

    @Override
    public void mudarPais() {
        int country = TanderaPreferences.getApplicationContry(mActivity);
        switch (country) {
            case 0:
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.country_pt_br)
                        .content(R.string.country_msg)
                        .iconRes(R.drawable.bra_flag)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.country_change)
                        .limitIconToDefaultSize()
                        .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
                        .onNegative((dialog, which) -> {
                            Intent intent = new Intent(mActivity, SettingsActivity.class);
                            mActivity.startActivity(intent);
                        })
                        .show();
                break;
            case 1:
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.country_en_us)
                        .content(R.string.country_msg)
                        .iconRes(R.drawable.usa_flag)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.country_change)
                        .limitIconToDefaultSize()
                        .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
                        .onNegative((dialog, which) -> {
                            Intent intent = new Intent(mActivity, SettingsActivity.class);
                            mActivity.startActivity(intent);
                        })
                        .show();
                break;
        }
    }

    @Override
    public void getDoacao() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.donate_dialog_title)
                .content(R.string.donate_dialog)
                .iconRes(R.drawable.coffee_cup)
                .limitIconToDefaultSize()
                .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
                .positiveText(R.string.of_course)
                .negativeText(R.string.no)
                .onPositive((dialog, which) -> {
                    Intent intent = new Intent(mActivity, DonateActivity.class);
                    mActivity.startActivity(intent);
                })
                .show();
    }

    @Override
    public void startJsonActivity(String name, int year, String type) {
        Intent intent = new Intent(mActivity, SearchAPIActivity.class);
        intent.putExtra("itemtitle", name);
        intent.putExtra("itemyear", year);
        intent.putExtra("itemtype", type);
        mActivity.startActivity(intent);
    }

}
