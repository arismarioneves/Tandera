package com.mari05lim.tandera.mvp.views;

import com.mari05lim.tandera.model.network.NetworkError;

import java.util.List;

/**
 * DEV Mari05liM
 */
public interface MvpLceListView<M> extends UiView {

    void showError(NetworkError error);

    void setData(List<M> data);

    void showLoadingProgress(boolean visible);

    void onRefreshData(boolean visible);
}