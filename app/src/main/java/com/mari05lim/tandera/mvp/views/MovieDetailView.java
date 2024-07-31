package com.mari05lim.tandera.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface MovieDetailView extends MvpLceView<MovieWrapper>  {

        void showMovieDetail(MovieWrapper movie, View ui);

        void playTrailer();

        void showPersonDetail(PersonWrapper person, View ui);

        void showMovieImages(MovieWrapper movie);

        void showMovieCreditsDialog(TanderaQueryType queryType);

}