package com.mari05lim.tandera.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.views.MovieImagesView;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.FetchMovieImagesRunnable;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.Collections;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class MovieImagesPresenter extends MvpPresenter<MovieImagesView> implements BaseDetailPresenter<MovieImagesView> {

    private String mRequeStParameter;

    private static final String LOG_TAG = MovieImagesPresenter.class.getSimpleName();

    public MovieImagesPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
    }

    @Override
    public void attachUiByParameter(MovieImagesView view, String requestedParameter) {
        final int callingId = getId(view);
        mRequeStParameter = requestedParameter;
        fetchMovieImagesIfNeeded(callingId, requestedParameter);
        populateUi(view, requestedParameter);
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
    public void populateUi(MovieImagesView view, String parameter) {
        final MovieWrapper movie = MoviesApp.get().getState().getMovie(parameter);

        if (movie != null && !MoviesCollections.isEmpty(movie.getBackdropImages())) {
            view.setData(Collections.unmodifiableList(movie.getBackdropImages()));
        }
    }

    @Override
    public void refresh(MovieImagesView view, String parameter) {
        //NTD
    }

    @Override
    public int getId(MovieImagesView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Subscribe
    public void onMovieImagesChanged(MoviesState.MovieImagesUpdatedEvent event) {
        Log.d(LOG_TAG, "On movie images received");
        populateUi(getViewState(), mRequeStParameter);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (null != event.error) {
            getViewState().showError(event.error);
        }
    }

    private void fetchMovieImagesIfNeeded(final int callingId, String parameter) {
        Preconditions.checkNotNull(parameter, "id cannot be null");

        MovieWrapper movie = MoviesApp.get().getState().getMovie(parameter);
        if (movie != null && MoviesCollections.isEmpty(movie.getBackdropImages())) {
            fetchMovieImages(callingId, parameter);
        }
    }

    private void fetchMovieImages(final int callingId, String parameter) {
        Preconditions.checkNotNull(parameter, "id cannot be null");

        MovieWrapper movie = MoviesApp.get().getState().getMovie(parameter);
        if (movie != null && movie.getTmdbId() != null) {
            executeNetworkTask(new FetchMovieImagesRunnable(callingId, movie.getTmdbId()));
        }
    }

}