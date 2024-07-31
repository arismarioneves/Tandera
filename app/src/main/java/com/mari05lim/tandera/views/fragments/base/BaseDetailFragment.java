package com.mari05lim.tandera.views.fragments.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.network.NetworkError;
import com.mari05lim.tandera.model.util.TextUtils;
import com.mari05lim.tandera.mvp.views.MvpLceView;
import com.mari05lim.tandera.util.StringUtils;
import com.mari05lim.tandera.views.listeners.AppBarStateChangeListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * DEV Mari05liM
 */
public abstract class BaseDetailFragment<M extends Serializable> extends BaseMvpFragment implements MvpLceView<M>{

    protected UltimateRecyclerView mPrimaryRecyclerView;

    @Nullable
    @BindView(R.id.appbar)
    protected AppBarLayout mAppBar;

    @Nullable
    @BindView(R.id.left_container)
    ViewGroup mLeftContainer;

    protected String mToolbarTitle;

    private int mLastFirstVisiblePosition;

    @Nullable
    @BindView(R.id.backdrop_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;

    protected RecyclerView.OnScrollListener expandableScrollListener = new RecyclerView.OnScrollListener() {
        int scrollDy = 0;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (scrollDy == 0 && (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)) {
                mAppBar.setExpanded(true);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollDy += dy;
        }
    };

    protected AppBarLayout.OnOffsetChangedListener offsetListener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State
                state) {
            if (state == AppBarStateChangeListener.State.EXPANDED || state == AppBarStateChangeListener.State.IDLE) {
                mCollapsingToolbarLayout.setTitle("");
            } else {
                mCollapsingToolbarLayout.setTitle(mToolbarTitle);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mPrimaryRecyclerView.getLayoutManager().scrollToPosition(mLastFirstVisiblePosition);

        if (mAppBar != null) {
            mAppBar.addOnOffsetChangedListener(offsetListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mLastFirstVisiblePosition = ((LinearLayoutManager)mPrimaryRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        if (mAppBar != null)
            mAppBar.removeOnOffsetChangedListener(offsetListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("FIRST_VISIBLE_POSITION", mLastFirstVisiblePosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_detail_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mLastFirstVisiblePosition = savedInstanceState.getInt("FIRST_VISIBLE_POSITION");
        }

        mPrimaryRecyclerView = view.findViewById(R.id.primary_recycler_view);
        mPrimaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        enableEmptyViewPolicy();
        getRecyclerView().disableLoadmore();

        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    public UltimateRecyclerView getRecyclerView() {
        return mPrimaryRecyclerView;
    }

    public boolean hasLeftContainer() {
        return mLeftContainer != null;
    }

    protected void enableEmptyViewPolicy() {
        mPrimaryRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_KEEP_HEADER_AND_LOARMORE);
    }

    @Override
    public void showError(NetworkError error) {
        toast(getText(StringUtils.getMessageByError(error)).toString());
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        getActivity().setProgressBarIndeterminateVisibility(visible);
    }

    public void toast(final String message) {
        if (!this.isResumed()) {
            return;
        }
        Toast.makeText(getBaseActivity(), TextUtils.toSpanned(getBaseActivity(), message, R.color.primary), Toast.LENGTH_SHORT).show();

    }

    /**
     * BaseViewHolder
     * @param <T>
     */
    abstract public class BaseViewHolder<T extends UltimateRecyclerviewViewHolder> {

        private final BaseDetailAdapter mDataBindAdapter;

        public BaseViewHolder(BaseDetailAdapter dataBindAdapter) {
            mDataBindAdapter = dataBindAdapter;
        }

        abstract public T newViewHolder(ViewGroup parent);

        abstract public void bindViewHolder(T holder, int position);

        abstract public int getItemCount();

        public final void notifyDataSetChanged() {
            mDataBindAdapter.notifyDataSetChanged();
        }

        public final void notifyBinderDataSetChanged() {
            notifyBinderItemRangeChanged(0, getItemCount());
        }

        public final void notifyBinderItemChanged(int position) {
            mDataBindAdapter.notifyBinderItemChanged(this, position);
        }

        public final void notifyBinderItemRangeChanged(int positionStart, int itemCount) {
            mDataBindAdapter.notifyBinderItemRangeChanged(this, positionStart, itemCount);
        }

        public final void notifyBinderItemInserted(int position) {
            mDataBindAdapter.notifyBinderItemInserted(this, position);
        }

        public final void notifyBinderItemMoved(int fromPosition, int toPosition) {
            mDataBindAdapter.notifyBinderItemMoved(this, fromPosition, toPosition);
        }

        public final void notifyBinderItemRangeInserted(int positionStart, int itemCount) {
            mDataBindAdapter.notifyBinderItemRangeInserted(this, positionStart, itemCount);
        }

        public final void notifyBinderItemRemoved(int position) {
            mDataBindAdapter.notifyBinderItemRemoved(this, position);
        }

        public final void notifyBinderItemRangeRemoved(int positionStart, int itemCount) {
            mDataBindAdapter.notifyBinderItemRangeRemoved(this, positionStart, itemCount);
        }
    }

    /**
     * BaseDetailAdapter
     */
    abstract public class BaseDetailAdapter extends UltimateViewAdapter<UltimateRecyclerviewViewHolder> {

        @Override
        public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return getDataBinder(viewType).newViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(UltimateRecyclerviewViewHolder holder, int position) {
            int binderPosition = getBinderPosition(position);
            getDataBinder(holder.getItemViewType()).bindViewHolder(holder, binderPosition);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public UltimateRecyclerviewViewHolder newFooterHolder(View view) {
            return new UltimateRecyclerviewViewHolder<>(view);
        }

        @Override
        public UltimateRecyclerviewViewHolder newHeaderHolder(View view) {
            return new UltimateRecyclerviewViewHolder<>(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public long generateHeaderId(int position) {
            return 0;
        }

        @Override
        public abstract int getItemCount();

        @Override
        public abstract int getItemViewType(int position);

        public abstract <T extends BaseViewHolder> T getDataBinder(int viewType);

        public abstract int getPosition(BaseViewHolder binder, int binderPosition);

        public abstract int getBinderPosition(int position);

        public void notifyBinderItemChanged(BaseViewHolder binder, int binderPosition) {
            notifyItemChanged(getPosition(binder, binderPosition));
        }

        public abstract void notifyBinderItemRangeChanged(BaseViewHolder binder, int positionStart,
                                                          int itemCount);

        public void notifyBinderItemInserted(BaseViewHolder binder, int binderPosition) {
            notifyItemInserted(getPosition(binder, binderPosition));
        }

        public void notifyBinderItemMoved(BaseViewHolder binder, int fromPosition, int toPosition) {
            notifyItemMoved(getPosition(binder, fromPosition), getPosition(binder, toPosition));
        }

        public abstract void notifyBinderItemRangeInserted(BaseViewHolder binder, int positionStart,
                                                           int itemCount);

        public void notifyBinderItemRemoved(BaseViewHolder binder, int binderPosition) {
            notifyItemRemoved(getPosition(binder, binderPosition));
        }

        public abstract void notifyBinderItemRangeRemoved(BaseViewHolder binder, int positionStart,
                                                          int itemCount);
    }

    /**
     * ListDetailAdapter
     */
    public class ListDetailAdapter extends BaseDetailAdapter {

        protected List<BaseViewHolder> mBinderList = new ArrayList<>();

        @Override
        public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            for (BaseViewHolder binder : mBinderList) {
                itemCount += binder.getItemCount();
            }
            return itemCount;
        }

        @Override
        public int getAdapterItemCount() {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            int itemCount = 0;
            for (int viewType = 0; viewType < mBinderList.size(); viewType++) {
                itemCount += mBinderList.get(viewType).getItemCount();
                if (position < itemCount) {
                    return viewType;
                }
            }
            throw new IllegalArgumentException("arg position is invalid");
        }

        @Override
        public <T extends BaseViewHolder> T getDataBinder(int viewType) {
            return (T) mBinderList.get(viewType);
        }

        @Override
        public int getPosition(BaseViewHolder binder, int binderPosition) {
            int viewType = mBinderList.indexOf(binder);
            if (viewType < 0) {
                throw new IllegalStateException("binder does not exist in adapter");
            }

            int position = binderPosition;
            for (int i = 0; i < viewType; i++) {
                position += mBinderList.get(i).getItemCount();
            }

            return position;
        }

        @Override
        public int getBinderPosition(int position) {
            int binderItemCount;
            for (int i = 0; i < mBinderList.size(); i++) {
                binderItemCount = mBinderList.get(i).getItemCount();
                if (position - binderItemCount < 0) {
                    break;
                }
                position -= binderItemCount;
            }
            return position;
        }

        @Override
        public void notifyBinderItemRangeChanged(BaseViewHolder binder, int positionStart, int itemCount) {
            notifyItemRangeChanged(getPosition(binder, positionStart), itemCount);
        }

        @Override
        public void notifyBinderItemRangeInserted(BaseViewHolder binder, int positionStart, int itemCount) {
            notifyItemRangeInserted(getPosition(binder, positionStart), itemCount);
        }

        @Override
        public void notifyBinderItemRangeRemoved(BaseViewHolder binder, int positionStart, int itemCount) {
            notifyItemRangeRemoved(getPosition(binder, positionStart), itemCount);
        }

        public List<BaseViewHolder> getBinderList() {
            return mBinderList;
        }

        public void addHeaderBinder(BaseViewHolder binder) {
            mBinderList.add(binder);
        }

        public void addBinder(BaseViewHolder binder) {
            mBinderList.add(binder);
        }

        public void addAllBinder(List<BaseViewHolder> dataSet) {
            mBinderList.addAll(dataSet);
        }

        public void addAllBinder(BaseViewHolder... dataSet) {
            mBinderList.addAll(Arrays.asList(dataSet));
        }

        public void setBinder(int index, BaseViewHolder binder) {
            mBinderList.set(index, binder);
        }

        public void removeBinder(int index) {
            mBinderList.remove(index);
        }

        public void removeBinder(BaseViewHolder binder) {
            mBinderList.remove(binder);
        }

        public void clearBinderList() {
            mBinderList.clear();
        }
    }

    /**
     * EnumListDetailAdapter
     * @param <E>
     */
    public abstract class EnumListDetailAdapter<E extends Enum<E>> extends ListDetailAdapter {

        public <T extends BaseViewHolder> T getDataBinder(E e) {
            return getDataBinder(e.ordinal());
        }
    }

}