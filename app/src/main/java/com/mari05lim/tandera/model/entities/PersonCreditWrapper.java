package com.mari05lim.tandera.model.entities;

import com.uwetrottmann.tmdb.entities.PersonCastCredit;
import com.uwetrottmann.tmdb.entities.PersonCrewCredit;

import java.util.Comparator;

/**
 * DEV Mari05liM
 */
public  class PersonCreditWrapper {

    public static final Comparator<PersonCreditWrapper> COMPARATOR_SORT_DATE
            = (movie, movie2) -> {
                if (movie.releaseDate > movie2.releaseDate) {
                    return -1;
                } else if (movie.releaseDate < movie2.releaseDate) {
                    return 1;
                }
                return 0;
            };

    final String title;
    final int id;
    final String job;
    final String posterPath;
    final long releaseDate;

    public PersonCreditWrapper(PersonCastCredit credit) {
        this.id = credit.id;
        this.title = credit.title;
        this.posterPath = credit.poster_path;
        this.job = credit.character;
        this.releaseDate = credit.release_date != null ? credit.release_date.getTime() : 0;
    }

    public PersonCreditWrapper(PersonCrewCredit credit) {
        this.id = credit.id;
        this.title = credit.title;
        this.posterPath = credit.poster_path;
        this.job = credit.job;
        this.releaseDate = credit.release_date != null ? credit.release_date.getTime() : 0;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

}