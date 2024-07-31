package com.mari05lim.tandera.model.state;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.entities.CreditWrapper;
import com.mari05lim.tandera.model.entities.Genre;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.SeasonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.uwetrottmann.tmdb.entities.CastMember;
import com.uwetrottmann.tmdb.entities.CrewMember;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.Person;
import com.uwetrottmann.tmdb.entities.TvSeason;
import com.uwetrottmann.tmdb.entities.TvShowComplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * DEV Mari05liM
 */
@Singleton
public class EntityMapper {

    final ApplicationState TanderaState;

    @Inject
    public EntityMapper(ApplicationState TanderaState) {
        this.TanderaState = Preconditions.checkNotNull(TanderaState, "TanderaState cannot be null");
    }

    MovieWrapper getMovieEntity(String id) {
       if (TanderaState.getTmdbIdMovies().containsKey(id)) {
            return TanderaState.getTmdbIdMovies().get(id);
        }
        return null;
    }

    void putMovieEntity(MovieWrapper movie) {
        if (movie.getTmdbId() != null) {
            TanderaState.getTmdbIdMovies().put(String.valueOf(movie.getTmdbId()), movie);
        }
    }

    PersonWrapper getPersonEntity(String id) {
        return TanderaState.getPeople().get(id);
    }

    void putPersonEntity(PersonWrapper entity) {
        TanderaState.getPeople().put(String.valueOf(entity.getTmdbId()), entity);
    }

    SeasonWrapper getSeasonEntity(String id) {
        return TanderaState.getTvSeasons().get(id);
    }

    void putSeasonEntity(SeasonWrapper entity) {
        TanderaState.getTvSeasons().put(String.valueOf(entity.getTmdbId()), entity);
    }

    ShowWrapper getShowEntity(String id) {
        return TanderaState.getTvShows().get(id);
    }

    ShowWrapper getShowEntity(int id) {
        return getShowEntity(String.valueOf(id));
    }

    void putShowEntity(ShowWrapper entity) {
        TanderaState.getTvShows().put(String.valueOf(entity.getTmdbId()), entity);
    }

    /*
    Wrap CrewMember to PersonWrapper
     */
    public PersonWrapper map(CrewMember entity) {
        PersonWrapper item = getPersonEntity(String.valueOf(entity.id));

        if (item == null) {
            // No item, so create one
            item = new PersonWrapper();
        }

        // We already have a movie, so just update it wrapped value
        item.set(entity);
        putPersonEntity(item);

        return item;
    }

    public List<CreditWrapper> mapCrewCredits(List<CrewMember> entities) {
        final ArrayList<CreditWrapper> credits = new ArrayList<>(entities.size());
        for (CrewMember entity : entities) {
            credits.add(new CreditWrapper(map(entity), entity.job, entity.department));
        }
        Collections.sort(credits);
        return credits;
    }

    /*
    Wrap CastMember to PersonWrapper
     */
    public PersonWrapper map(CastMember entity) {
        PersonWrapper item = getPersonEntity(String.valueOf(entity.id));

        if (item == null) {
            // No item, so create one
            item = new PersonWrapper();
        }

        // We already have a movie, so just update it wrapped value
        item.set(entity);
        putPersonEntity(item);

        return item;
    }

    public List<CreditWrapper> mapCastCredits(List<CastMember> entities) {
        final ArrayList<CreditWrapper> credits = new ArrayList<>(entities.size());
        for (CastMember entity : entities) {
            credits.add(new CreditWrapper(map(entity), entity.character, entity.order));
        }
        Collections.sort(credits);
        return credits;
    }

    /*
    Wrap Person to PersonWrapper
     */
    public PersonWrapper map(Person entity) {
        PersonWrapper item = getPersonEntity(String.valueOf(entity.id));

        if (item == null) {
            // No item, so create one
            item = new PersonWrapper();
        }

        // We already have a movie, so just update it wrapped value
        item.set(entity);
        putPersonEntity(item);

        return item;
    }

    /*
    Wrap Movie to MovieWrapper
     */
    public MovieWrapper map(Movie entity) {
        MovieWrapper movie = getMovieEntity(String.valueOf(entity.id));

        if (movie == null && entity.imdb_id != null) {
            movie = getMovieEntity(entity.imdb_id);
        }

        if (movie == null) {
            // No movie, so create one
            movie = new MovieWrapper(String.valueOf(entity.id));
        }
        // We already have a movie, so just update it wrapped value
        movie.setFromMovie(entity);
        putMovieEntity(movie);

        return movie;
    }

    /*
    Wrap TvShowComplete to ShowWrapper
     */
    public ShowWrapper map(TvShowComplete entity) {
        ShowWrapper show = getShowEntity(String.valueOf(entity.id));

        if (show == null && entity.id != null) {
            show = getShowEntity(entity.id);
        }

        if (show == null) {
            // No show, so create one
            show = new ShowWrapper(String.valueOf(entity.id));
        }
        // We already have a movie, so just update it wrapped value
        show.setFromShow(entity);
        putShowEntity(show);

        return show;
    }

    /*
    Wrap TvSeason to SeasonWrapper
    */
    public SeasonWrapper map(TvSeason entity) {
        SeasonWrapper item = getSeasonEntity(String.valueOf(entity.id));

        if (item == null) {
            // No item, so create one
            item = new SeasonWrapper();
        }

        // We already have a season entity, so just update it wrapped value
        item.setFromSeason(entity);
        putSeasonEntity(item);

        return item;
    }

    public List<SeasonWrapper> mapTvSeasons(Integer tvId, List<TvSeason> entities) {
        final ShowWrapper show = getShowEntity(String.valueOf(tvId));
        if (show != null) {
            final ArrayList<SeasonWrapper> seasons = new ArrayList<>(entities.size());
            for (TvSeason season : entities) {
                seasons.add(map(season));
            }
            return seasons;
        }
        return null;
    }

    public static List<Genre> mapGenres(List<com.uwetrottmann.tmdb.entities.Genre> entities) {
        final ArrayList<Genre> genres = new ArrayList<>();
        for (com.uwetrottmann.tmdb.entities.Genre tmdbGenre : entities) {
            Genre value = Genre.fromName(tmdbGenre.id);
            if (value != null) {
                genres.add(value);
            }
        }
        //Collections.sort(genres);
        return genres;
    }

}