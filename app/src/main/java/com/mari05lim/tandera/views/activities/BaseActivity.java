package com.mari05lim.tandera.views.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.arellomobile.mvp.MvpDelegate;
import com.mari05lim.tandera.MoviesDisplay;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.views.custom_views.TanderaToolbar;

import butterknife.ButterKnife;

/**
 * DEV Mari05liM
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Display mDisplay;
    @StyleRes
    private int appliedTheme;
    private boolean savedState;
    private MvpDelegate<? extends BaseActivity> mMvpDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.appliedTheme = getThemeResId();
        setTheme(this.appliedTheme);
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        getMvpDelegate().onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());
        ButterKnife.bind(this);

        setDisplay();
        handleIntent(getIntent(), getDisplay());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mDisplay = null;

        getMvpDelegate().onDestroyView();

        if (isFinishing()) {
            getMvpDelegate().onDestroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getMvpDelegate().onAttach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.savedState = false;
        getMvpDelegate().onAttach();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMvpDelegate().onDetach();
    }

    public MvpDelegate getMvpDelegate()
    {
        if (mMvpDelegate == null)
        {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }

    protected void setupActionBar() {
        TanderaToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar, true);
            toolbar.setToolbarTitleTypeface();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        this.savedState = true;

        super.onSaveInstanceState(outState, outPersistentState);
        getMvpDelegate().onSaveInstanceState(outState);
        getMvpDelegate().onDetach();
    }

    @StyleRes
    protected abstract int getThemeResId();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, getDisplay());
    }

    protected abstract void handleIntent(Intent intent, Display display);

    protected void setDisplay() {
        mDisplay = new MoviesDisplay(this);
    }

    protected boolean navigateUp() {
        final Intent intent = getParentIntent();
        if (intent != null) {
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return false;
    }

    protected Intent getParentIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    protected int getContentViewLayoutId() {
        return R.layout.activity_main;
    }

    public Display getDisplay() {
        return mDisplay;
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar, boolean handleBackground) {
        setSupportActionBar(toolbar);
        getDisplay().setSupportActionBar(toolbar, handleBackground);
    }

    protected boolean popLastFragment() {
        if ((!isFinishing()) && (!this.savedState))
        {
            FragmentManager localFragmentManager = getFragmentManager();
            if (localFragmentManager.getBackStackEntryCount() > 0)
                return localFragmentManager.popBackStackImmediate();
        }
        return false;
    }

}