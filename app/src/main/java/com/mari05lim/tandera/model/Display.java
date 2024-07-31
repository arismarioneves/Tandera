package com.mari05lim.tandera.model;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;

/**
 * DEV Mari05liM
 */
public interface Display {

    String PARAM_ID = "_id";
    String PARAM_LOCATION = "_location";
    String PARAM_POSITION = "_image";
    String PARAM_IMAGE_BACKGROUND = "_image_background";
    String PARAM_SEARCH_TYPE = "_search_type";

    void setDrawerLayout(DrawerLayout layout);

    boolean hasSearchFragment();

    void showWatched();

    void showMovies();

    void showTvShows();

    void showStream();

    void showSettings();

    void showAbout();

    void showRelatedMovies(String movieId);

    void startWatchlistActivity();

    void startSearchListActivity(String listType, Bundle bundle);

    void startMovieDetailActivityBySharedElements(String movieId, View view, String imageUrl);

    void startMovieDetailActivity(String movieId, Bundle bundle);

    void showMovieDetailFragment(String movieId);

    Fragment showMovieDetailFragmentBySharedElements(String movieId);

    void startTvDetailActivity(String showId, Bundle bundle);

    void startTvDetailActivityBySharedElements(String tvId, View view, String imageUrl);

    void showTvDetailFragment(String tvId);

    Fragment showTvDetailFragmentBySharedElement(String tvId);

    void startPersonDetailActivity(String id, Bundle bundle);

    void startPersonDetailActivityBySharedElement(String id, View view);

    void showPersonDetailFragment(String id);

    Fragment showPersonFragment(String tvId);

    void startStreamActivity();

    void startSettingsActivity();

    void startAboutActivity();

    void startJsonActivity(String name, int year, String type);

    void playYoutubeVideo(String id);

    void shareMovie(int movieId, String movieTitle);

    void shareTvShow(int showId, String showTitle);

    void startShareIntentChooser(String message, @StringRes int titleResId);

    void openTmdbMovie(MovieWrapper movie);

    void openTmdbPerson(PersonWrapper person);

    void openTmdbTvShow(ShowWrapper show);

    void openTmdbUrl(String url);

    boolean tryStartActivity(Intent intent, boolean displayError);

    void performWebSearch(String query);

    void showSearchFragment();

    void showSearchMoviesFragment();

    void showSearchPeopleFragment();

    void showSearchTvShowsFragment();

    void closeDrawerLayout();

    boolean hasMainFragment();

    void showUpNavigation(boolean show);

    void setActionBarTitle(CharSequence title);

    void setActionBarSubtitle(CharSequence title);

    boolean popEntireFragmentBackStack();

    void finishActivity();

    boolean toggleDrawer();

    void setStatusBarColor(float scroll);

    void setSupportActionBar(Object toolbar, boolean handleBackground);

    boolean isTablet();

    void sendEmail();

    void mudarPais();

    void getDoacao();
}