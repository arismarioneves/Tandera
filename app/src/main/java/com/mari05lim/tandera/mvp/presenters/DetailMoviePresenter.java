package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.views.MovieDetailView;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.DatabaseBackgroundRunnable;
import com.mari05lim.tandera.model.tasks.FetchDetailMovieRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntitySeenRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntityUnseenRunnable;
import com.squareup.otto.Subscribe;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class DetailMoviePresenter extends MvpPresenter<MovieDetailView> implements BaseDetailPresenter<MovieDetailView> {

    private String mRequestParameter;

    private static final String LOG_TAG = DetailMoviePresenter.class.getSimpleName();

    public DetailMoviePresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
        mRequestParameter = null;
    }

    @Override
    public int getId(MovieDetailView view) {
        return view.hashCode();
    }

    @Override
    public String getUiTitle(String parameter) {
        final MovieWrapper movie = MoviesApp.get().getState().getMovie(parameter);
        if (movie != null) {
            return movie.getTitle();
        }
        return null;
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Override
    public void attachUiByParameter(MovieDetailView view, String requestedParameter) {
        final int callingId = getId(view);
        mRequestParameter = requestedParameter;
        fetchDetailMovieIfNeeded(callingId, requestedParameter);
        populateUi(view, mRequestParameter);
    }

    @Override
    public void refresh(MovieDetailView view, String parameter) {
        final int callingId = getId(view);
        fetchDetailMovie(callingId, parameter);
    }

    public void checkDetailMovieResult(int callingId, MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        fetchDetailMovieIfNeeded(callingId, movie, false);
    }

    public void toggleMovieWatched(MovieDetailView view, MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        final int callingId = getId(view);
        if (movie.isWatched()) {
            markMovieUnseen(callingId, movie);
        } else {
            markMovieSeen(callingId, movie);
        }
    }

    @Override
    public void populateUi(MovieDetailView view, String parameter) {
        final MovieWrapper movie = MoviesApp.get().getState().getMovie(parameter);
        if (movie != null) {
            view.updateDisplayTitle(movie.getTitle());
            view.setData(movie);
        }
    }

    @Subscribe
    public void onMovieDetailChanged(MoviesState.MovieInformationUpdatedEvent event) {
        populateUi(getViewState(), mRequestParameter);
        checkDetailMovieResult(event.callingId, event.item);
    }

    @Subscribe
    public void onMovieWatchedChanged(MoviesState.MovieFlagUpdateEvent event) {
        populateUi(getViewState(), mRequestParameter);
    }


    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (null != event.error) {
            getViewState().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if(!event.secondary) {
            getViewState().showLoadingProgress(event.show);
        }
    }

    /**
     * Fetch detail movie information
     */
    public void fetchDetailMovie(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        final MovieWrapper movie = MoviesApp.get().getState().getMovie(id);
        if (movie != null) {
            fetchDetailMovieIfNeeded(callingId, movie, true);
        }
    }

    public void fetchDetailMovieFromTmdb(final int callingId, int id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper movie = MoviesApp.get().getState().getMovie(id);
        if (movie != null) {
            movie.markFullFetchStarted();
        }

        executeNetworkTask(new FetchDetailMovieRunnable(callingId, id));
    }

    public void fetchDetailMovieIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper cached = MoviesApp.get().getState().getMovie(id);
        if (cached == null) {
            fetchDetailMovie(callingId, id);
        } else {
            fetchDetailMovieIfNeeded(callingId, cached, false);
        }
    }

    public void fetchDetailMovieIfNeeded(int callingId, MovieWrapper movie, boolean force) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        if (force || movie.needFullFetchFromTmdb()) {
            if (movie.getTmdbId() != null) {
                fetchDetailMovieFromTmdb(callingId, movie.getTmdbId());
            }
        }
    }

    public void markMovieSeen(int callingId, MovieWrapper movie) {
        executeBackgroundTask(new MarkEntitySeenRunnable(callingId, movie));
    }

    public void markMovieUnseen(int callingId, MovieWrapper movie) {
        executeBackgroundTask(new MarkEntityUnseenRunnable(callingId, movie));
    }

}