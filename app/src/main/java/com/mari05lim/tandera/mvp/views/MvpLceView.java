package com.mari05lim.tandera.mvp.views;

import com.mari05lim.tandera.model.network.NetworkError;

/**
 * DEV Mari05liM
 */
public interface MvpLceView<M> extends UiView {
    /**
     * Show the error view.
     * <b>The error view must be a TextView with the id = R.id.mErrorView</b>
     *  @param error The Throwable that has caused this error
     *
     */
    void showError(NetworkError error);

    void setData(M data);

    void showLoadingProgress(boolean visible);

    void onRefreshData(boolean visible);

}