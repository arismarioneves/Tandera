package com.mari05lim.tandera.mvp.views;

import com.arellomobile.mvp.GenerateViewState;
import com.arellomobile.mvp.MvpView;

/**
 * DEV Mari05liM
 */
@GenerateViewState
public interface MainView extends MvpView {

    void setData(int[] data);

}