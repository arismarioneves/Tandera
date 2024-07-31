package com.mari05lim.tandera.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.state.MoviesState;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface SearchView extends MvpLceView<MoviesState.SearchResult> {

    void showMovieDetail(MovieWrapper movie, View item);

    void showTvShowDetail(ShowWrapper show, View item);

    void showPersonDetail(PersonWrapper person, View item);

}