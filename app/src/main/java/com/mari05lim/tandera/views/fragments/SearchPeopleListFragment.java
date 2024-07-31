package com.mari05lim.tandera.views.fragments;

import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.mari05lim.tandera.mvp.presenters.ListPeoplePresenter;
import com.mari05lim.tandera.mvp.views.ListPeopleView;
import com.mari05lim.tandera.views.adapters.PeopleListAdapter;
import com.mari05lim.tandera.views.fragments.base.BaseListFragment;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.PersonWrapper;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class SearchPeopleListFragment extends BaseListFragment<PeopleListAdapter.PeopleListViewHolder, PersonWrapper> implements ListPeopleView {

    @InjectPresenter
    ListPeoplePresenter mPresenter;

    @Override
    public TanderaQueryType getQueryType() {
        return TanderaQueryType.SEARCH_PEOPLE;
    }

    @Override
    protected easyRegularAdapter<PersonWrapper, PeopleListAdapter.PeopleListViewHolder> createAdapter(List<PersonWrapper> data) {
        return new PeopleListAdapter(data, this);
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void onClick(View view, int position) {
        PersonWrapper item = mAdapter.getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void showItemDetail(PersonWrapper person, View view) {
        Preconditions.checkNotNull(person, "person cannot be null");
        Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), null);
        }
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }

}