package com.mari05lim.tandera.views.fragments.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mari05lim.tandera.R;

import java.util.ArrayList;
import java.util.List;

/**
 * DEV Mari05liM
 */
public abstract class BaseTabFragment<E extends BaseTabFragment.TabPagerAdapter> extends BaseMvpFragment {

    private static final String SAVE_SELECTED_TAB = "selected_tab";

    private ViewPager mViewPager;
    private TabLayout mSlidingTabStrip;
    private E mAdapter;

    protected int mCurrentItem;

    final private TabLayout.TabLayoutOnPageChangeListener mOnPageChangeListener = new TabLayout.TabLayoutOnPageChangeListener(mSlidingTabStrip) {
        @Override
        public void onPageScrollStateChanged(int state) {
            //do nothing
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //do nothing
        }

        @Override
        public void onPageSelected(int position) {
            onPageChanged(position);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);

        mAdapter = setupAdapter();

        mViewPager = view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setOffscreenPageLimit(3);

        mSlidingTabStrip = view.findViewById(R.id.tabs);
        mSlidingTabStrip.post(new Runnable() {
            @Override
            public void run() {
                mSlidingTabStrip.setupWithViewPager(mViewPager);
            }
        });


        if (savedInstanceState != null) {
            mCurrentItem = savedInstanceState.getInt(SAVE_SELECTED_TAB);
        }

        return view;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.tab_fragment_viewpager;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_SELECTED_TAB, mCurrentItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentItem = mViewPager.getCurrentItem();
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    protected abstract E setupAdapter();

    public E getAdapter() {
        return mAdapter;
    }

    protected TabLayout getSlidingTabStrip() {
        return mSlidingTabStrip;
    }

    protected void setFragments(List<Fragment> fragments) {
        mAdapter.setFragments(fragments);
        mViewPager.setCurrentItem(mCurrentItem);
    }

    protected  abstract String getTabTitle(int position);

    /**
     * This method will be called when an item in the Tab Layout will be selected.
     * Subclasses should override.
     *
     * @param position The position of the view in the Tab Layout
     */
    public void onPageChanged(int position) {
    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<Fragment> mFragments;

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
        }

       void setFragments(List<Fragment> fragments)  {
           mFragments.clear();
           mFragments.addAll(fragments);
           notifyDataSetChanged();
       }

        @Override
        public Fragment getItem(int position) {
                return mFragments.get(position);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return getTabTitle(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}