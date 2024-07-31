package com.mari05lim.tandera.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.views.TvShowDetailView;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.DatabaseBackgroundRunnable;
import com.mari05lim.tandera.model.tasks.FetchDetailTvShowRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntitySeenRunnable;
import com.mari05lim.tandera.model.tasks.MarkEntityUnseenRunnable;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class DetailShowPresenter extends MvpPresenter<TvShowDetailView> implements BaseDetailPresenter<TvShowDetailView> {

    private static final String LOG_TAG = DetailShowPresenter.class.getSimpleName();

    private String mRequeStParameter;
    private UiView.TanderaQueryType mCurrentQuery;

    public DetailShowPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
        mRequeStParameter = null;
        mCurrentQuery = null;
    }

    @Override
    public void attachUiByParameter(TvShowDetailView view, String requestedParameter) {
        final int callingId = getId(view);
        fetchDetailTvShowIfNeeded(callingId, requestedParameter);
    }

    public void attachUiByQuery(TvShowDetailView view, String requestedParameter, UiView.TanderaQueryType queryType) {
        mRequeStParameter = requestedParameter;
        mCurrentQuery = queryType;
        switch (queryType) {
            case TV_SHOW_DETAIL:
                attachUiByParameter(view, requestedParameter);
                break;
            case TV_SEASONS_LIST: {
                //TODO
                break;
            }
        }
        populateUi(view, requestedParameter);
    }

    @Override
    public String getUiTitle(String parameter) {
        final ShowWrapper tv =  MoviesApp.get().getState().getTvShow(parameter);
        if (tv != null) {
            return tv.getTitle();
        }
        return null;
    }

    @Override
    public void populateUi(TvShowDetailView view, String parameter) {
        final ShowWrapper show = MoviesApp.get().getState().getTvShow(parameter);

        switch (mCurrentQuery) {
            case TV_SHOW_DETAIL:
                if (show != null) {
                    view.setData(show);
                }
                break;
            case TV_SEASONS_LIST: {
                if (show != null && !MoviesCollections.isEmpty(show.getSeasons())) {
                    view.updateDisplaySubtitle(getUiSubtitle());
                    view.setSeasons(show.getSeasons());
                }
                break;
            }
        }
    }

    @Override
    public void refresh(TvShowDetailView view, String parameter) {
        final int callingId = getId(view);
        fetchDetailTvShow(callingId, parameter);
    }

    @Override
    public int getId(TvShowDetailView view) {
        return view.hashCode();
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

    private void checkDetailTvShowResult(int callingId, ShowWrapper show) {
        Preconditions.checkNotNull(show, "show cannot be null");
        fetchDetailTvShowIfNeeded(callingId, show, false);
    }

    @Subscribe
    public void onShowDetailChanged(MoviesState.TvShowInformationUpdatedEvent event) {
        populateUi(getViewState(), mRequeStParameter);
        checkDetailTvShowResult(event.callingId, event.item);
    }

    @Subscribe
    public void onShowWatchedChanged(MoviesState.ShowFlagUpdateEvent event) {
        populateUi(getViewState(), mRequeStParameter);
    }

    @Subscribe
    public void onTvSeasonDetailsChanged(MoviesState.TvShowSeasonUpdatedEvent event) {
        /*populateUi();
        checkDetailSeasonResult(event.callingId, event.item, event.secondaryItem);*/
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (null != event.error) {
            getViewState().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if (!event.secondary) {
            getViewState().showLoadingProgress(event.show);
        }
    }

    public String getUiSubtitle() {
        return null;
    }

    /**
     * Fetch detail movie information
     */
    private void fetchDetailTvShow(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        final ShowWrapper tvShow = MoviesApp.get().getState().getTvShow(id);
        if (tvShow != null) {

            fetchDetailTvShowIfNeeded(callingId, tvShow, true);
        }
    }

    private void fetchDetailTvShowFromTmdb(final int callingId, int id) {
        Preconditions.checkNotNull(id, "id cannot be null");
        ShowWrapper show = MoviesApp.get().getState().getTvShow(id);
        if (show != null) {
            show.markFullFetchStarted();
        }
        executeNetworkTask(new FetchDetailTvShowRunnable(callingId, id));
    }

    private void fetchDetailTvShowIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        ShowWrapper cached = MoviesApp.get().getState().getTvShow(id);
        if (cached == null) {
            Log.d(LOG_TAG, "cached == null");
            fetchDetailTvShow(callingId, id);
        } else {
            Log.d(LOG_TAG, "cached != null");
            fetchDetailTvShowIfNeeded(callingId, cached, false);
        }
    }

    private void fetchDetailTvShowIfNeeded(int callingId, ShowWrapper show, boolean force) {
        Preconditions.checkNotNull(show, "show cannot be null");

        if (force || show.needFullFetchFromTmdb()) {
            if (show.getTmdbId() != null) {
                Log.d(LOG_TAG, "show.getTmdbId() != null");
                fetchDetailTvShowFromTmdb(callingId, show.getTmdbId());
            }
        }
    }

    public void toggleShowWatched(TvShowDetailView view, ShowWrapper show) {
        Preconditions.checkNotNull(show, "show cannot be null");
        final int callingId = getId(view);
        if (show.isWatched()) {
            markShowUnseen(callingId, show);
        } else {
            markShowSeen(callingId, show);
        }
    }

    private void markShowSeen(int callingId, ShowWrapper show) {
        executeBackgroundTask(new MarkEntitySeenRunnable(callingId, show));
    }

    private void markShowUnseen(int callingId, ShowWrapper show) {
        executeBackgroundTask(new MarkEntityUnseenRunnable(callingId, show));
    }

}