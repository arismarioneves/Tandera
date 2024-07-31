package com.mari05lim.tandera.views.fragments.base;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpDelegate;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.util.UiUtils;
import com.mari05lim.tandera.views.activities.BaseActivity;
import com.mari05lim.tandera.views.custom_views.TanderaToolbar;

import butterknife.ButterKnife;

/**
 * DEV Mari05liM
 */
public abstract class BaseMvpFragment extends Fragment implements BaseUiView {

    private boolean mIsStateSaved;
    private MvpDelegate<? extends BaseMvpFragment> mMvpDelegate;

    private TanderaToolbar mToolbar;

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract void attachUiToPresenter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout wrapper = new FrameLayout(getActivity());
        inflater.inflate(getLayoutRes(), wrapper, true);
        ButterKnife.bind(this, wrapper);
        return wrapper;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsStateSaved = true;
        getMvpDelegate().onSaveInstanceState(outState);
        getMvpDelegate().onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        attachUiToPresenter();
        mIsStateSaved = false;
        getMvpDelegate().onAttach();
    }

    public void onResume() {
        super.onResume();
        mIsStateSaved = false;
        getMvpDelegate().onAttach();
    }

    @Override
    public void onStop() {
        super.onStop();
        getMvpDelegate().onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getMvpDelegate().onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //We leave the screen and respectively all fragments will be destroyed
        if (getActivity().isFinishing()) {
            getMvpDelegate().onDestroy();
            return;
        }

        // When we rotate device isRemoving() return true for fragment placed in backstack
        // http://stackoverflow.com/questions/34649126/fragment-back-stack-and-isremoving
        if (mIsStateSaved) {
            mIsStateSaved = false;
            return;
        }

        // See https://github.com/Arello-Mobile/Moxy/issues/24
        boolean anyParentIsRemoving = false;

        if (Build.VERSION.SDK_INT >= 17) {
            Fragment parent = getParentFragment();
            while (!anyParentIsRemoving && parent != null) {
                anyParentIsRemoving = parent.isRemoving();
                parent = parent.getParentFragment();
            }
        }

        if (isRemoving() || anyParentIsRemoving) {
            getMvpDelegate().onDestroy();
        }
    }

    /**
     * @return The {@link MvpDelegate} being used by this Fragment.
     */
    public MvpDelegate getMvpDelegate()
    {
        if (mMvpDelegate == null)
        {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar = view.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setToolbarTitleTypeface();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        UiUtils.getInstance().applyFontToMenu(menu, getActivity());
    }

    protected  void setSupportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar, true);
    }

    protected void setSupportActionBar(Toolbar toolbar, boolean handle) {
        getBaseActivity().setSupportActionBar(toolbar, handle);
    }

    public TanderaToolbar getToolbar() {
        return mToolbar;
    }

    protected Display getDisplay() {
        return getBaseActivity().getDisplay();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity)getActivity();
    }

}