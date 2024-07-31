package com.mari05lim.tandera.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.mari05lim.tandera.model.entities.PersonWrapper;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface ListPeopleView extends BaseListView<PersonWrapper> {

    void showItemDetail(PersonWrapper item, View ui);

}
