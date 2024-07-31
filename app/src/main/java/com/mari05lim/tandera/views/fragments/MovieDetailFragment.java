package com.mari05lim.tandera.views.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.CreditWrapper;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.TrailerWrapper;
import com.mari05lim.tandera.model.util.ImageHelper;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.mari05lim.tandera.mvp.presenters.DetailMoviePresenter;
import com.mari05lim.tandera.mvp.views.MovieDetailView;
import com.mari05lim.tandera.util.FlagUrlProvider;
import com.mari05lim.tandera.views.activities.SettingsActivity;
import com.mari05lim.tandera.views.custom_views.MovieDetailCardLayout;
import com.mari05lim.tandera.views.custom_views.MovieDetailInfoLayout;
import com.mari05lim.tandera.views.custom_views.MovieTitleCardLayout;
import com.mari05lim.tandera.views.custom_views.MovieWatchedToggler;
import com.mari05lim.tandera.views.custom_views.RatingBarLayout;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;
import com.mari05lim.tandera.views.custom_views.TanderaTextView;
import com.mari05lim.tandera.views.custom_views.ViewRecycler;
import com.mari05lim.tandera.views.fragments.base.BaseAnimationFragment;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * DEV Mari05liM
 */
public class MovieDetailFragment extends BaseAnimationFragment<MovieWrapper> implements MovieDetailView {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String KEY_MOVIE_SAVE_STATE = "movie_on_save_state";

    @InjectPresenter
    DetailMoviePresenter mPresenter;

    @Override
    public void onRefreshData(boolean visible) {
    }

    @Inject
    FlagUrlProvider mFlagUrlProvider;

//    private static final int ANIMATION_DELAY = 150;
//    long animationDelay = ANIMATION_DELAY + 2 * 30;

    private DetailAdapter mAdapter;
    private MovieWrapper mMovie;
    private final ArrayList<DetailItemType> mItems = new ArrayList<>();

    private MenuItem mYoutubeItem;
    private MenuItem mShareItem;
    private MenuItem mWebSearchItem;
    private MenuItem mWatchedItem;

    boolean isEnableYoutube = false;
    boolean isEnableShare = false;

    private RelativeLayout mSummaryContainer;

    private TanderaImageView mFanartImageView;
    private TextView mTitleTextView;
    private TextView mSummary;
    private RatingBarLayout mRatingBarLayout;

    private RelatedMoviesAdapter mRelatedMoviesAdapter;
    private MovieCastAdapter mMovieCastAdapter;
    private MovieCrewAdapter mMovieCrewAdapter;
    private MovieTrailersAdapter mMovieTrailersAdapter;

    @Nullable @BindView(R.id.toggler_watched)
    MovieWatchedToggler mWatchedToggler;
    @Nullable @BindView(R.id.button_trailer)
    TextView mTrailerButton;

    private static final Date DATE = new Date();

    protected static final String QUERY_MOVIE_ID = "movie_id";

    @Override
    protected void configureEnterTransition() {
        if (mPosterImageView != null) {
            ViewCompat.setTransitionName(mPosterImageView, getActivity().getString(R.string.transition_poster));
        }
    }

    private void onWatchedClicked() {
        if (!mMovie.isWatched()) {
            toast(String.format(getResources().getString(R.string.action_item_added_to_watched), mMovie.getTitle()));
        }
        mPresenter.toggleMovieWatched(this, mMovie);
    }

    private void updateMovieWatched(boolean isWatched) {
        if (mWatchedToggler != null)
        mWatchedToggler.setStatus(isWatched);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onFabClicked() {
        getDisplay().startJsonActivity(mMovie.getTitle(), mMovie.getYear(), this.getResources().getString(R.string.movie));
    }

    public static  MovieDetailFragment newInstance(String id) {
        Preconditions.checkArgument(id != null, "movieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, id);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static MovieDetailFragment newInstance(String movieId, String imagePosition) {
        Preconditions.checkArgument(movieId != null, "movieId can not be null");
        Preconditions.checkArgument(imagePosition != null, "ImageUrl can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        bundle.putString(KEY_IMAGE_POSITION, imagePosition);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public String getRequestParameter() {
        return getArguments().getString(QUERY_MOVIE_ID);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_movie_detail_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MoviesApp.from(getActivity()).inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_MOVIE_SAVE_STATE, mMovie);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            setData((MovieWrapper) savedInstanceState.getSerializable(KEY_MOVIE_SAVE_STATE));
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                @TargetApi(21)
                public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
                }
            });
        }

        mSummaryContainer = view.findViewById(R.id.container_layout);
        mFanartImageView = view.findViewById(R.id.fanart_image);
        mTitleTextView = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        if (mPosterImageView != null) {

            // check if jelly bean or higher
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mFanartImageView.setImageAlpha(150);
            } else {
                mFanartImageView.setAlpha(150);
            }
            mPosterImageView.setOnClickListener(v -> showMovieImages(mMovie));
        } else {

            mFanartImageView.setOnClickListener(v -> showMovieImages(mMovie));
        }

        mRatingBarLayout = view.findViewById(R.id.rating_bar);

        if (hasLeftContainer()) {
            mWatchedToggler.setOnClickListener(v -> onWatchedClicked());

            //onPrepareTrailerButton(mTrailerButton);
            mTrailerButton.setOnClickListener(v -> {
            if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                playTrailer();
            }
            });
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail, menu);
        mWatchedItem = menu.findItem(R.id.menu_item_watched);

        mYoutubeItem = menu.findItem(R.id.menu_open_youtube);
        mYoutubeItem.setEnabled(isEnableYoutube);
        mYoutubeItem.setVisible(isEnableYoutube);

        mShareItem = menu.findItem(R.id.menu_movie_share);
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem = menu.findItem(R.id.menu_action_movie_websearch);
        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        isEnableYoutube = (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers()));
        mYoutubeItem.setEnabled(isEnableYoutube);
        mYoutubeItem.setVisible(isEnableYoutube);

        isEnableShare = mMovie != null && !TextUtils.isEmpty(
                mMovie.getTitle());
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);

        if (mWatchedItem != null) {
            if (mMovie != null) {
                if (mMovie.isWatched()) {
                    mWatchedItem.setIcon(R.drawable.ic_added_to_watchlist);
                    mWatchedItem.setTitle(R.string.menu_item_movie_library);
                } else {
                    mWatchedItem.setIcon(R.drawable.ic_add_to_watchlist);
                    mWatchedItem.setTitle(R.string.menu_item_watchable_add_to_watched);
                }
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    public void onPrepareTrailerButton(TextView view) {
        isEnableYoutube = (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers()));
        //view.setEnabled(isEnableYoutube);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Display display = getDisplay();
        if (display != null) {
            switch (item.getItemId()) {
                case R.id.menu_item_watched: {
                    onWatchedClicked();
                    return true;
                }
                case R.id.menu_refresh: {
                    mPresenter.refresh(this, getRequestParameter());
                    return true;
                }
                case R.id.menu_movie_share: {
                    if (mMovie.getTmdbId() != null) {
                        display.shareMovie(mMovie.getTmdbId(), mMovie.getTitle());
                    }
                }
                return true;
                case R.id.menu_open_youtube: {
                    if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                      playTrailer();
                    }
                }
                return true;
//                case R.id.menu_open_tmdb: {
//                    if (mMovie.getTmdbId() != null) {
//                        display.openTmdbMovie(mMovie);
//                    }
//                }
//                return true;
                case R.id.menu_action_movie_websearch: {
                    if (mMovie.getTitle() != null) {
                        display.performWebSearch(mMovie.getTitle());
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * MovieDetailView
     */
    @Override
    protected void attachUiToPresenter() {
        mPresenter.attachUiByParameter(this, getRequestParameter());
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
            display.setActionBarTitle(mPresenter.getUiTitle(getRequestParameter()));
        }
    }

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {
        //TODO
    }

    @Override
    public TanderaQueryType getQueryType() {
        return TanderaQueryType.MOVIE_DETAIL;
    }

    @Override
    public void setData(MovieWrapper data) {
        mMovie = data;
        mAdapter = populateUi();
        mToolbarTitle = data.getTitle();
        if (mAdapter != null)
            getRecyclerView().setAdapter(mAdapter);

        updateMovieWatched(mMovie.isWatched());
        mFanartImageView.setVisibility(View.VISIBLE);

        if (mSummaryContainer != null) {
            mSummaryContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), null);
            }
        }
    }

    @Override
    public void showMovieImages(MovieWrapper movie) {
//        Preconditions.checkNotNull(movie, "movie cannot be null");
//
//        final Display display = getDisplay();
//        if (display != null) {
//            display.startMovieImagesActivity(String.valueOf(movie.getTmdbId()));
//        }
    }

    @Override
    public void showPersonDetail(PersonWrapper person, View view) {
        Preconditions.checkNotNull(person, "person cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), null);
        }
    }

    @Override
    public void playTrailer() {
        final Display display = getDisplay();
        if (display != null) {
            for (TrailerWrapper trailer : mMovie.getTrailers()) {
                if (trailer.getSource().equals(TrailerWrapper.Source.YOUTUBE)) {
                    display.playYoutubeVideo(trailer.getId());
                    return;
                }
            }
        }
    }

    @Override
    public void showMovieCreditsDialog(TanderaQueryType queryType) {
        Preconditions.checkNotNull(queryType, "Query type cannot be null");
        ListView list = new ListView(getActivity());
        list.setDivider(null);
        String mTitle = "";
        boolean wrapInScrollView = false;

        switch (queryType) {
            case MOVIE_CAST:
                list.setAdapter(getMovieCastAdapter());
                mTitle = getResources().getString(R.string.cast_movies);
                break;
            case MOVIE_CREW:
                list.setAdapter(getMovieCrewAdapter());
                mTitle = getResources().getString(R.string.crew_movies);
                break;
            case RELATED_MOVIES:
                list.setAdapter(getRelatedMoviesAdapter());
                mTitle = getResources().getString(R.string.related_movies);
        }
        new MaterialDialog.Builder(getActivity())
                .title(mTitle)
                .customView(list, wrapInScrollView)

                .backgroundColorRes(R.color.primary)
                .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
                .show();
    }

    protected DetailAdapter createRecyclerAdapter(List<DetailItemType> items) {
        return new DetailAdapter(items);
    }

    private DetailAdapter populateUi() {
        if (mMovie == null) {
            return null;
        }
        mItems.clear();

        if (mMovie.hasBackdropUrl()) {
            mFanartImageView.loadBackdrop(mMovie);
        }

        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setTitle(mMovie.getTitle());
        }

        if (hasLeftContainer()) {
            if (mMovie.getYear() > 0) {
                mTitleTextView.setText(mMovie.getTitle() + " (" + mMovie.getYear() + ")");
            } else {
                mTitleTextView.setText(mMovie.getTitle());
            }

            mSummary.setText(mMovie.getOverview());

            mPosterImageView.loadPoster(mMovie);

            mRatingBarLayout.setRatingVotes(mMovie.getRatingVotes());
            mRatingBarLayout.setRatingValue(mMovie.getRatingVoteAverage());
            mRatingBarLayout.setRatingRange(10);

        } else {

            mItems.add(DetailItemType.TITLE);

            if (!TextUtils.isEmpty(mMovie.getOverview())) {
                mItems.add(DetailItemType.SUMMARY);
            }
        }

        mItems.add(DetailItemType.DETAILS);

        /*
         *  if (!MoviesCollections.isEmpty(mMovie.getTrailers())){
         *  mItems.add(DetailItemType.TRAILERS);
         *  }
         */

        if (!MoviesCollections.isEmpty(mMovie.getCast())) {
            mItems.add(DetailItemType.CAST);
        }

        if (!MoviesCollections.isEmpty(mMovie.getCrew())) {
            mItems.add(DetailItemType.CREW);
        }

        if (!MoviesCollections.isEmpty(mMovie.getRelated())) {
            mItems.add(DetailItemType.RELATED);
        }

        if (mItems.size() >= 4) {
            if (mAppBar != null)
                mPrimaryRecyclerView.addOnScrollListener(expandableScrollListener);
        }

        return createRecyclerAdapter(mItems);
    }

     enum DetailItemType  {
         TITLE,          //(R.layout.item_movie_detail_title_old), includes poster, tagline and rating
         DETAILS,        //(R.layout.item_movie_detail_details), include details
         SUMMARY,        //(R.layout.item_movie_detail_summary), includes description text, maybe
         TRAILERS,       //(R.layout.item_movie_detail_trailers), includes trailers
         RELATED,        //(R.layout.item_movie_detail_generic_card), includes related movies list
         CAST,           //(R.layout.item_movie_detail_generic_card), includes cast list
         CREW            //(R.layout.item_movie_detail_generic_card), includes crew list
    }

    /**
     * DetailAdapter
     */
    public class DetailAdapter extends  EnumListDetailAdapter<DetailItemType> {
        List<BaseViewHolder> mItems;

        public DetailAdapter() {
        }

        public DetailAdapter(List<DetailItemType> items) {
            mItems = new ArrayList<>(items.size());
            for (DetailItemType item : items) {
                switch (item) {
                    case TITLE:
                        mItems.add(new MovieTitleBinder(this));
                        break;
                    case SUMMARY:
                        mItems.add(new MovieDescriptionBinder(this));
                        break;
                    case DETAILS:
                        mItems.add(new MovieDetailsBinder(this));
                        break;
                    case TRAILERS:
                        mItems.add(new MovieTrailersBinder(this));
                        break;
                    case CAST:
                        mItems.add(new MovieCastBinder(this));
                        break;
                    case CREW:
                        mItems.add(new MovieCrewBinder(this));
                        break;
                    case RELATED:
                        mItems.add(new MovieRelatedBinder(this));
                }
            }
            addAllBinder(mItems);
        }
    }

    private void loadFlagImage(final String countryCode, final MovieDetailInfoLayout layout) {
        final String flagUrl = mFlagUrlProvider.getCountryFlagUrl(countryCode);
        final int width = getResources()
                .getDimensionPixelSize(R.dimen.country_flag_width);
        final int height = getResources()
                .getDimensionPixelSize(R.dimen.country_flag_height);

        final String url = ImageHelper.getResizedUrl(flagUrl, width, height);

        Picasso.get()
            .load(url)
            .into(layout);
    }

    /**
     * MovieDescriptionBinder
     */
    public class MovieDescriptionBinder extends BaseViewHolder<MovieDescriptionBinder.ViewHolder> {

        public MovieDescriptionBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_summary, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            holder.summary.setText(mMovie.getOverview());

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            View container;
            TextView summary;

            public ViewHolder(View view) {
                super(view);

                container = view.findViewById(R.id.movie_detail_card_details);
                summary = view.findViewById(R.id.textview_summary);
            }
        }
    }

    /**
     * MovieTitleBinder
     */
    public class MovieTitleBinder extends BaseViewHolder<MovieTitleBinder.ViewHolder> {

        public MovieTitleBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_title, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            holder.container.setTitle(mMovie.getTitle());
            holder.tagline.setText(mMovie.getTagline());

            holder.releaseYear.setText(String.valueOf(mMovie.getYear()));

            holder.posterImageView.loadPoster(mMovie);

            holder.ratingBarLayout.setRatingVotes(mMovie.getRatingVotes());
            holder.ratingBarLayout.setRatingValue(mMovie.getRatingVoteAverage());
            holder.ratingBarLayout.setRatingRange(10);

            holder.watchedButton.setStatus(mMovie.isWatched());

            holder.trailerButton.setOnClickListener(v -> {
                if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                    playTrailer();
                }
            });

            holder.watchedButton.setOnClickListener(v -> onWatchedClicked());
        }


        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            MovieTitleCardLayout container;
            TanderaTextView tagline;
            TanderaTextView releaseYear;
            TanderaImageView posterImageView;
            RatingBarLayout ratingBarLayout;
            TextView trailerButton;
            MovieWatchedToggler watchedButton;

            public ViewHolder(View view) {
                super(view);

                container = view.findViewById(R.id.movie_detail_card_title);
                tagline = view.findViewById(R.id.textview_tagline);
                releaseYear = view.findViewById(R.id.textview_release_year);
                posterImageView = view.findViewById(R.id.poster_image);
                ratingBarLayout = view.findViewById(R.id.rating_bar);
                trailerButton = view.findViewById(R.id.button_trailer);
                watchedButton = view.findViewById(R.id.toggler_watched);

            }
        }
    }

    /**
     * MovieDetailsBinder
     */
    public class MovieDetailsBinder extends BaseViewHolder<MovieDetailsBinder.ViewHolder> {

        public MovieDetailsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            if (mMovie.getRuntime() > 0) {
                holder.runtimeLayout.setContentText(
                        getString(R.string.movie_details_runtime_content, mMovie.getRuntime()));
                holder.runtimeLayout.setVisibility(View.VISIBLE);
            } else {
                holder.runtimeLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mMovie.getCertification())) {
                holder.certificationLayout.setContentText(mMovie.getCertification());
                holder.certificationLayout.setVisibility(View.VISIBLE);
            } else {
                holder.certificationLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mMovie.getGenresString())) {
                holder.genreLayout.setContentText(mMovie.getGenresString());
                holder.genreLayout.setVisibility(View.VISIBLE);
            } else {
                holder.genreLayout.setVisibility(View.GONE);
            }

            if (mMovie.getReleasedTime() > 0) {
                DATE.setTime(mMovie.getReleasedTime());
                DateFormat dateFormat = DateFormat.getDateInstance();
                holder.releasedInfoLayout.setContentText( dateFormat.format(DATE));
                holder.releasedInfoLayout.setVisibility(View.VISIBLE);

//                final String countryCode = mMovie.getReleasedCountryCode();
//                if (!TextUtils.isEmpty(countryCode)) {
//                    loadFlagImage(countryCode, holder.releasedInfoLayout);
//                }

            } else {
                holder.releasedInfoLayout.setVisibility(View.GONE);
            }

            if (mMovie.getBudget() > 0) {
                holder.budgetInfoLayout.setContentText(
                        getString(R.string.movie_details_budget_content, mMovie.getBudget()));
                holder.budgetInfoLayout.setVisibility(View.VISIBLE);
            } else {
                holder.budgetInfoLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mMovie.getMainLanguageTitle())) {
                holder.languageLayout.setContentText(mMovie.getMainLanguageTitle());
                holder.languageLayout.setVisibility(View.VISIBLE);
            } else {
                holder.languageLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            MovieDetailCardLayout container;
            MovieDetailInfoLayout runtimeLayout;
            MovieDetailInfoLayout certificationLayout;
            MovieDetailInfoLayout genreLayout;
            MovieDetailInfoLayout releasedInfoLayout;
            MovieDetailInfoLayout budgetInfoLayout;
            MovieDetailInfoLayout languageLayout;

            public ViewHolder(View view) {
                super(view);

                container = view.findViewById(R.id.movie_detail_card_details);
                runtimeLayout = view.findViewById(R.id.layout_info_runtime);
                certificationLayout = view.findViewById(R.id.layout_info_certification);
                genreLayout = view.findViewById(R.id.layout_info_genres);
                releasedInfoLayout = view.findViewById(R.id.layout_info_released);
                budgetInfoLayout = view.findViewById(R.id.layout_info_budget);
                languageLayout = view.findViewById(R.id.layout_info_language);
            }
        }
    }

    /**
     * MovieCastBinder
     */
    public class MovieCastBinder extends BaseViewHolder<MovieCastBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public MovieCastBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind cast");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;

            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            cardLayout.setTitle(R.string.cast_movies);

            final View.OnClickListener seeMoreClickListener = v -> showMovieCreditsDialog(TanderaQueryType.MOVIE_CAST);

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getMovieCastAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getMovieCastAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getMovieCastAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getMovieCastAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     * MovieTitleBinder
     */
    public class MovieCrewBinder extends BaseViewHolder<MovieCrewBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public MovieCrewBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind crew");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            cardLayout.setTitle(R.string.crew_movies);

            final View.OnClickListener seeMoreClickListener = v -> showMovieCreditsDialog(TanderaQueryType.MOVIE_CREW);

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getMovieCrewAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getMovieCrewAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getMovieCrewAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getMovieCrewAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);

                layout = view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     * MovieRelated
     */
    public class MovieRelatedBinder extends BaseViewHolder<MovieRelatedBinder.ViewHolder> {

        private MovieDetailCardLayout cardLayout;

        public MovieRelatedBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            cardLayout.setTitle(R.string.related_movies);

            final View.OnClickListener seeMoreClickListener = v -> {
                if (getDisplay() != null) {
                    getDisplay().showRelatedMovies(String.valueOf(mMovie.getTmdbId()));
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getRelatedMoviesAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getRelatedMoviesAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getRelatedMoviesAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getRelatedMoviesAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);

                layout = view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     * MovieTrailersBinder
     */
    public class MovieTrailersBinder extends BaseViewHolder<MovieTrailersBinder.ViewHolder> {

        public MovieTrailersBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "bindTrailers");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_trailers, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getMovieTrailersAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getMovieTrailersAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getMovieTrailersAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     * BaseMovieCastAdapter
     */
    private abstract class BaseMovieCastAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final View.OnClickListener mItemOnClickListener;

        public BaseMovieCastAdapter(LayoutInflater mInflater, View.OnClickListener mItemOnClickListener) {
            this.mInflater = mInflater;
            this.mItemOnClickListener = mItemOnClickListener;
        }

        @Override
        public abstract CreditWrapper getItem(int position) ;

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(getLayoutId(), parent, false);
            }

            CreditWrapper credit = getItem(position);

            final TextView title = convertView.findViewById(R.id.title);
            title.setText(credit.getPerson().getName());

            final TanderaImageView image = convertView.findViewById(R.id.imageview_poster);
            image.setAvatarMode(true);
            //Load with Picasso
            image.loadProfile(credit.getPerson());


            final TextView subTitle = convertView.findViewById(R.id.textview_subtitle_1);
            if (subTitle != null) {
                if (!TextUtils.isEmpty(credit.getJob())) {
                    subTitle.setText(credit.getJob());
                    subTitle.setVisibility(View.VISIBLE);
                } else {
                    subTitle.setVisibility(View.GONE);
                }
            }

            convertView.setOnClickListener(mItemOnClickListener);
            convertView.setTag(credit);

            return convertView;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_2line;
        }
    }

    /**
     * ShowCastAdapter
     */
    private class MovieCastAdapter extends BaseMovieCastAdapter {

        public MovieCastAdapter(LayoutInflater mInflater) {
            super(mInflater, v -> {
            CreditWrapper cast = (CreditWrapper) v.getTag();
            if (cast != null) {
               showPersonDetail(cast.getPerson(), v);
            }
            });
        }

        @Override
        public int getCount() {
            return mMovie != null ? MoviesCollections.size(mMovie.getCast()) : 0;
        }

        @Override
        public CreditWrapper getItem(int position) {
            return mMovie.getCast().get(position);
        }
    }

    /**
     * ShowCrewAdapter
     */
    private class MovieCrewAdapter extends BaseMovieCastAdapter {

        public MovieCrewAdapter(LayoutInflater mInflater) {
            super(mInflater, v -> {
            CreditWrapper cast = (CreditWrapper) v.getTag();
            if (cast != null) {
                showPersonDetail(cast.getPerson(), v);
            }
            });
        }

        @Override
        public int getCount() {
            return mMovie != null ? MoviesCollections.size(mMovie.getCrew()) : 0;
        }

        @Override
        public CreditWrapper getItem(int position) {
            return mMovie.getCrew().get(position);
        }
    }

     /**
      *  RelatedMoviesAdapter
     */
    private class RelatedMoviesAdapter extends BaseAdapter {

        private final View.OnClickListener mItemOnClickListener;
        private final LayoutInflater mInflater;

        public RelatedMoviesAdapter( LayoutInflater mInflater) {
            this.mInflater = mInflater;

            this.mItemOnClickListener = v -> showMovieDetail((MovieWrapper) v.getTag(), v);
        }

        @Override
        public int getCount() {
            if (mMovie != null & !MoviesCollections.isEmpty(mMovie.getRelated())) {
                return mMovie.getRelated().size();
            } else
            return 0;
        }

        @Override
        public MovieWrapper getItem(int position) {
            return mMovie.getRelated().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(getLayoutId(), parent, false);
            }

            final MovieWrapper movie = getItem(position);

            final TextView title = convertView.findViewById(R.id.title);
            if (movie.getYear() > 0) {
                title.setText(getString(R.string.movie_title_year,
                        movie.getTitle(), movie.getYear()));
            } else {
                title.setText(movie.getTitle());
            }

            final TanderaImageView imageView =
                    convertView.findViewById(R.id.imageview_poster);
            imageView.setAvatarMode(false);
            imageView.loadPoster(movie);

            convertView.setOnClickListener(mItemOnClickListener);
            convertView.setTag(movie);

            return convertView;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_1line;
        }
    }

     /**
      *  MovieTrailersAdapter
     */
    private class MovieTrailersAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final View.OnClickListener mOnClickListener;

        public MovieTrailersAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;

            this.mOnClickListener = v -> {
            TrailerWrapper trailer = (TrailerWrapper) v.getTag();
            if (trailer != null) {
                playTrailer();
            }
            };
        }

        @Override
        public int getCount() {
            if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                return mMovie.getTrailers().size();
            } else {
                return 0;
            }
        }

        @Override
        public TrailerWrapper getItem(int position) {
            return mMovie.getTrailers().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           if (convertView == null) {
               convertView = mInflater.inflate(R.layout.item_movie_trailer, parent, false);
           }
            final TrailerWrapper trailer = getItem(position);

            TextView title = convertView.findViewById(R.id.title);
            title.setText(trailer.getName());

            convertView.setOnClickListener(mOnClickListener);
            convertView.setTag(trailer);

            return convertView;
        }
    }

    private RelatedMoviesAdapter getRelatedMoviesAdapter() {
        if (mRelatedMoviesAdapter == null) {
            mRelatedMoviesAdapter = new RelatedMoviesAdapter(LayoutInflater.from(getActivity()));
        }
        return mRelatedMoviesAdapter;
    }

    private MovieCastAdapter getMovieCastAdapter() {
        if (mMovieCastAdapter == null) {
            mMovieCastAdapter = new MovieCastAdapter(LayoutInflater.from(getActivity()));
        }
        return mMovieCastAdapter;
    }

    private MovieCrewAdapter getMovieCrewAdapter() {
        if (mMovieCrewAdapter == null) {
            mMovieCrewAdapter = new MovieCrewAdapter(LayoutInflater.from(getActivity()));
        }
        return mMovieCrewAdapter;
    }

    private MovieTrailersAdapter getMovieTrailersAdapter() {
        if (mMovieTrailersAdapter == null) {
            mMovieTrailersAdapter = new MovieTrailersAdapter(LayoutInflater.from(getActivity()));
        }
        return mMovieTrailersAdapter;
    }

    @Override
    protected void setSupportActionBar(Toolbar toolbar) {
       setSupportActionBar(toolbar, false);
    }

}