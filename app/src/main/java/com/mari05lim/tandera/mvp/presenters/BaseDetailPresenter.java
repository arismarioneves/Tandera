package com.mari05lim.tandera.mvp.presenters;

import com.mari05lim.tandera.mvp.views.UiView;

/**
 * DEV Mari05liM
 */
public interface BaseDetailPresenter<M extends UiView> extends BasePresenter<M> {

    void attachUiByParameter(M view, String requestedParameter);

    void populateUi(M view, String parameter);

    void refresh(M view, String parameter);

    String getUiTitle(String parameter);

}