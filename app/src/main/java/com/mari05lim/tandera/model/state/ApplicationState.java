package com.mari05lim.tandera.model.state;

import android.os.Build;
import android.util.ArrayMap;

import androidx.annotation.RequiresApi;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.entities.Entity;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.SeasonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.entities.TmdbConfiguration;
import com.mari05lim.tandera.model.entities.Watchable;
import com.mari05lim.tandera.model.repository.Repository;
import com.mari05lim.tandera.model.util.FileLog;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DEV Mari05liM
 */
public  class ApplicationState implements BaseState, MoviesState {

    private static final int INITIAL_MOVIE_MAP_CAPACITY = 200;

    private final Bus mEventBus;

    private final Map<String, MovieWrapper> mMovie;
    private final Map<String, PersonWrapper> mPeople;
    private final Map<String, ShowWrapper> mShows;
    private Map<String, SeasonWrapper> mSeasons;

    private List<Watchable> mWatched;

    private Map<Class<? extends Entity>, Repository<? extends Entity>> mRepositories;

    private MoviePaginatedResult mPopularMovies;
    private MoviePaginatedResult mNowPlayingMovies;
    private MoviePaginatedResult mUpcomingMovies;

    private ShowPaginatedResult mPopularShows;
    private ShowPaginatedResult mOnTheAirShows;

    private SearchResult mSearchResult;

    private TmdbConfiguration mConfiguration;

    private boolean mPopulatedWatchedFromDb;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ApplicationState(Bus mEventBus) {
        this.mEventBus = Preconditions.checkNotNull(mEventBus, "eventBus cannot be null");
        mMovie = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
        mShows = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
        mPeople = new ArrayMap<>();
        mSeasons = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
        mWatched = new ArrayList<>();
        mRepositories = new HashMap<>();
    }

    @Override
    public void registerForEvents(Object receiver) {
        mEventBus.register(receiver);
    }

    @Override
    public void unregisterForEvents(Object receiver) {
        mEventBus.unregister(receiver);
    }

    /*
    MovieState implementation
     */
    @Override
    public Map<String, MovieWrapper> getTmdbIdMovies() {
        return mMovie;
    }


    @Override
    public void putMovie(MovieWrapper movie) {
        if (movie.getTmdbId() != null) {
            mMovie.put(String.valueOf(movie.getTmdbId()), movie);
        }
    }

    @Override
    public void putShow(ShowWrapper show) {
        if (show.getTmdbId() != null) {
            mShows.put(String.valueOf(show.getTmdbId()), show);
        }
    }

    @Override
    public MoviePaginatedResult getPopularMovies() {
        return mPopularMovies;
    }

    @Override
    public void setPopularMovies(int callingId, MoviePaginatedResult popular) {
        mPopularMovies = popular;
        mEventBus.post(new PopularMoviesChangedEvent(callingId));
    }

    public MoviePaginatedResult getNowPlaying() {
        return mNowPlayingMovies;
    }

    public void setNowPlaying(int callingId, MoviePaginatedResult mNowPlaying) {
        this.mNowPlayingMovies = mNowPlaying;
        mEventBus.post(new InTheatresMoviesChangedEvent(callingId));
    }

    public MoviePaginatedResult getUpcoming() {
        return mUpcomingMovies;
    }

    public void setUpcoming(int callingId, MoviePaginatedResult mUpcoming) {
        this.mUpcomingMovies = mUpcoming;
        mEventBus.post(new UpcomingMoviesChangedEvent(callingId));
    }

    @Override
    public TmdbConfiguration getTmdbConfiguration() {
        return mConfiguration;
    }

    @Override
    public void setTmdbConfiguration(TmdbConfiguration configuration) {
        mConfiguration = configuration;
        mEventBus.post(new TmdbConfigurationChangedEvent());

    }

    @Override
    public Map<String, PersonWrapper> getPeople() {
        return mPeople;
    }

    @Override
    public PersonWrapper getPerson(int id) {
        return getPerson(String.valueOf(id));
    }

    @Override
    public PersonWrapper getPerson(String id) {
        return mPeople.get(id);
    }

    @Override
    public MovieWrapper getMovie(int id) {
        return getMovie(String.valueOf(id));
    }

    @Override
    public MovieWrapper getMovie(String id) {
        MovieWrapper movie = mMovie.get(id);
        return movie;
    }

    @Override
    public Map<String, ShowWrapper> getTvShows() {
        return mShows;
    }

    @Override
    public ShowWrapper getTvShow(String id) {
        return mShows.get(id);
    }

    public Map<String, SeasonWrapper> getTvSeasons() {
        return mSeasons;
    }

    public void setTvSeasons(Map<String, SeasonWrapper> mSeasons) {
        this.mSeasons = mSeasons;
    }

    @Override
    public ShowWrapper getTvShow(int id) {
        return getTvShow(String.valueOf(id));
    }

    @Override
    public SeasonWrapper getTvSeason(String id) {
        return mSeasons.get(id);
    }

    @Override
    public SeasonWrapper getTvSeason(int id) {
        return getTvSeason(String.valueOf(id));
    }

    @Override
    public ShowPaginatedResult getPopularShows() {
        return mPopularShows;
    }

    @Override
    public void setPopularShows(int callingId, ShowPaginatedResult popular) {
        this.mPopularShows = popular;
        mEventBus.post(new PopularShowsChangedEvent(callingId));
    }

    @Override
    public ShowPaginatedResult getOnTheAirShows() {
        return mOnTheAirShows;
    }

    @Override
    public void setOnTheAirShows(int callingId, ShowPaginatedResult onTheAir) {
        this.mOnTheAirShows = onTheAir;
        mEventBus.post(new OnTheAirShowsChangedEvent(callingId));
    }

    @Override
    public SearchResult getSearchResult() {
        return mSearchResult;
    }

    @Override
    public void setSearchResult(int callingId, SearchResult result) {
        mSearchResult = result;
        mEventBus.post(new SearchResultChangedEvent(callingId));
    }

    public List<Watchable> getWatched() {
        return mWatched;
    }


    public void setWatchedCleared() {
        mWatched.clear();
        mEventBus.post(new WatchedClearedEvent());
    }

    public void setWatched(List<Watchable> items) {
        if (!Objects.equal(items, mWatched)) {
            mWatched = items;
        }
        mEventBus.post(new WatchedChangedEvent());
    }

    public boolean isPopulatedWatchedFromDb() {
        return mPopulatedWatchedFromDb;
    }

    public void setPopulatedWatchedFromDb(boolean populated) {
        FileLog.d("watched", "AppState : setPopulatedFrom Db = " + populated);
        mPopulatedWatchedFromDb = populated;
    }

    public Map<Class<? extends Entity>, Repository<? extends Entity>> getRepositories() {
        return mRepositories;
    }

    public void setRepositories(Map<Class<? extends Entity>, Repository<? extends Entity>> mRepositories) {
        this.mRepositories = mRepositories;
    }

    public <M extends Entity> Repository getRepositoryInstance(Class<M> repositoryClass) {
        return mRepositories.get(repositoryClass);
    }

    public interface Callback<T> {
        void onFinished(T result);
    }

}