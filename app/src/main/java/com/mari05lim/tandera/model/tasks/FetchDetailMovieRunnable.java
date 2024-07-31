package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.AppendToResponse;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.enumerations.AppendToResponseItem;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchDetailMovieRunnable extends BaseRunnable<Movie> {

    private final int mId;

    public FetchDetailMovieRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Movie doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().summary(mId,
            getCountryProvider().getLetterLanguageCode(),
            new AppendToResponse(
                AppendToResponseItem.CREDITS,
                AppendToResponseItem.RELEASES,
                AppendToResponseItem.VIDEOS,
                AppendToResponseItem.SIMILAR
            )
        );
    }

    @Override
    public void onSuccess(Movie result) {

        MovieWrapper movie = getEntityMapper().map(result);
        movie.markFullFetchCompleted();

        // Releases depends on country, need to update manually
        if (result.releases != null) {
            movie.updateReleases(result.releases,
                    getCountryProvider().getLetterCountryCode());
        }

        //Releases should be mapped due to entity mapper
        if (result.similar_movies != null) {
            List<MovieWrapper> movies = new ArrayList<>(result.similar_movies.results.size());
            for (Movie mMovie: result.similar_movies.results) {
                movies.add(getEntityMapper().map(mMovie));
            }
            movie.setRelated(movies);
        }

        if (result.credits != null && !MoviesCollections.isEmpty(result.credits.cast)){
            movie.setCast(getEntityMapper().mapCastCredits(result.credits.cast));
        }

        if (result.credits != null && !MoviesCollections.isEmpty(result.credits.crew)){
            movie.setCrew(getEntityMapper().mapCrewCredits(result.credits.crew));
        }

        getEventBus().post(new MoviesState.MovieInformationUpdatedEvent(getCallingId(), movie));
    }

    @Override
    public void onError(RetrofitError re) {
        if (re.getResponse() != null && re.getResponse().getStatus() == 404) {
            MovieWrapper movie = mState.getMovie(mId);
            if (movie != null) {

                getEventBus()
                        .post(new MoviesState.MovieInformationUpdatedEvent(getCallingId(), movie));
            }
        }
        super.onError(re);
    }

}