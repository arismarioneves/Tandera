package com.mari05lim.tandera.model.entities;

import com.uwetrottmann.tmdb.entities.CastMember;
import com.uwetrottmann.tmdb.entities.CrewMember;
import com.uwetrottmann.tmdb.entities.Person;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DEV Mari05liM
 */
public class PersonWrapper implements Serializable {

    public Integer tmdbId;
    public String name;
    public String pictureUrl;

    public String placeOfBirth;
    public Date dateOfBirth;
    public Date dateOfDeath;
    public int age;
    public String biography;

    public int pictureType;

    transient List<PersonCreditWrapper> castCredits;
    transient List<PersonCreditWrapper> crewCredits;
    transient boolean isfetchedCredits;

    public void set(CrewMember member) {
        tmdbId = member.id;
        name = member.name;
        pictureUrl = member.profile_path;
    }

    public void set(CastMember member) {
        tmdbId = member.id;
        name = member.name;
        pictureUrl = member.profile_path;
    }

    public void set(Person person) {
        tmdbId = person.id;
        name = person.name;
        pictureUrl = person.profile_path;
        biography = person.biography;
        dateOfBirth = person.birthday;
        dateOfDeath = person.deathday;
        placeOfBirth = person.place_of_birth;

        calculateAge();
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public String getName() {
        return name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public Integer getAge() {
        return age;
    }

    public String getBiography() {
        return biography;
    }

    public int getPictureType() {
        return pictureType;
    }

    public List<PersonCreditWrapper> getCastCredits() {
        return castCredits;
    }

    public List<PersonCreditWrapper> getCrewCredits() {
        return crewCredits;
    }

    public void setCastCredits(List<PersonCreditWrapper> castCredits) {
        this.castCredits = castCredits;
    }

    public void setCrewCredits(List<PersonCreditWrapper> crewCredits) {
        this.crewCredits = crewCredits;
    }

    public boolean isFetchedCredits() {
        return isfetchedCredits;
    }

    public void setfetchedCredits(boolean fetchedCredits) {
        this.isfetchedCredits = fetchedCredits;
    }

    private void calculateAge() {
        if (dateOfBirth != null) {
            long endDate = dateOfDeath != null ? dateOfDeath.getTime() : System.currentTimeMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(endDate - dateOfBirth.getTime());
            age = cal.get(Calendar.YEAR) - 1970;
        }
    }

}