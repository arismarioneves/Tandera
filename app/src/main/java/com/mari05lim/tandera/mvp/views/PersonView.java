package com.mari05lim.tandera.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.PersonCreditWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface PersonView extends MvpLceView<PersonWrapper>{

    void showMovieDetail(PersonCreditWrapper credit, View item);
    void showPersonCreditsDialog(TanderaQueryType queryType);

}