package com.mari05lim.tandera.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.presenters.MainPresenter;
import com.mari05lim.tandera.mvp.views.MainView;
import com.mari05lim.tandera.model.Display;

/**
 * DEV Mari05liM
 */
public class WatchlistActivity extends BaseNavigationActivity implements MainView{

    @InjectPresenter
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animateManually(true);
        mDrawerLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                //Should be implemented in fragments
                return false;
            }
            case  R.id.menu_search:
                getDisplay().showSearchFragment();
                return true;
        }
        return false;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (!display.hasMainFragment()) {
                display.showSearchFragment();
            }
        } else  {
            if (!display.hasMainFragment()) {
                display.showMovies();
            }
        }
    }

    @Override
    public void setData(int[] data) {
        updateHeader(data);
    }

}