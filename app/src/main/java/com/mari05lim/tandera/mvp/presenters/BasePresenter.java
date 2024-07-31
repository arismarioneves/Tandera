package com.mari05lim.tandera.mvp.presenters;

import com.arellomobile.mvp.MvpView;
import com.mari05lim.tandera.model.tasks.BaseRunnable;

/**
 * DEV Mari05liM
 */
 public interface BasePresenter<M extends MvpView>{

    int TMDB_FIRST_PAGE = 1;

    int getId(M view);

    <BR> void executeNetworkTask(BaseRunnable<BR> task);

}