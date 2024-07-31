package com.mari05lim.tandera.mvp.presenters;

import com.mari05lim.tandera.mvp.views.BaseListView;
import com.mari05lim.tandera.mvp.views.UiView;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.state.MoviesState;

/**
 * DEV Mari05liM
 */
public interface BaseListPresenter<M extends BaseListView> extends BasePresenter<M> {

    int TMDB_FIRST_PAGE = 1;

    void onUiAttached(M view, UiView.TanderaQueryType queryType, String parameter);

    String getUiTitle(UiView.TanderaQueryType queryType);

    String getUiSubtitle(UiView.TanderaQueryType queryType);

    void populateUi(M view, UiView.TanderaQueryType queryType);

    boolean canFetchNextPage(ApplicationState.PaginatedResult<?> paginatedResult);

    void refresh(M view, UiView.TanderaQueryType queryType);

    void onScrolledToBottom(M view, UiView.TanderaQueryType queryType);

    void populateUiFromEvent(MoviesState.BaseEvent event, UiView.TanderaQueryType queryType);

    M findUi(final int id);

}