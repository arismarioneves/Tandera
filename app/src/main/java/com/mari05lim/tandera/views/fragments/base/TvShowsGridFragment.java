package com.mari05lim.tandera.views.fragments.base;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.mvp.views.ListTvShowsView;
import com.mari05lim.tandera.util.AnimUtils;
import com.mari05lim.tandera.util.TanderaPreferences;
import com.mari05lim.tandera.views.activities.SettingsActivity;
import com.mari05lim.tandera.views.adapters.ShowsGridAdapter;
import com.mari05lim.tandera.views.custom_views.TvShowDialogView;
import com.mari05lim.tandera.views.custom_views.recyclerview.AutofitGridLayoutManager;
import com.mari05lim.tandera.views.custom_views.recyclerview.RecyclerInsetsDecoration;
import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;

import java.util.List;

/**
 * DEV Mari05liM
 */
public abstract class TvShowsGridFragment extends BaseGridFragment<ShowsGridAdapter.ShowViewHolder, ShowWrapper> implements ListTvShowsView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int width = (int)getActivity().getResources().getDimension(R.dimen.show_grid_item_width);
        getRecyclerView().setLayoutManager(new AutofitGridLayoutManager(getActivity(), width , getAdapter()));
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity(), NavigationGridType.SHOWS));

        //set actionbar up navigation
        final Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_show_recycler;
    }

    public TanderaQueryType getQueryType() {
        return TanderaQueryType.COMMON_SHOWS;
    }

    @Override
    protected UltimateGridLayoutAdapter<ShowWrapper, ShowsGridAdapter.ShowViewHolder> createAdapter(List<ShowWrapper> data) {
        return new ShowsGridAdapter(data, getActivity(),getMvpDelegate(), this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:{
                onRefreshData(hasAdapter());
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {

    }

    @Override
    public void updateDisplayTitle(String title) {

    }

    @Override
    public void showItemDetail(ShowWrapper tvShow, View view) {
        Preconditions.checkNotNull(tvShow, "tv cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (tvShow.getTmdbId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && TanderaPreferences.areAnimationsEnabled(getContext())
                        && view.getTag() != null) {
                    display.startTvDetailActivityBySharedElements(String.valueOf(tvShow.getTmdbId()), view, (String) view.getTag());
                } else {
                    display.startTvDetailActivity(String.valueOf(tvShow.getTmdbId()), null);
                }
            }
        }
    }

    @Override
    public void showContextMenu(final ShowWrapper tvShow) {
        final TvShowDialogView dialogView = new TvShowDialogView(getActivity());

        MaterialDialog localMaterialDialog = new MaterialDialog.Builder(getActivity())
            .title(tvShow.getTitle())
            .autoDismiss(false)
            .customView(dialogView, true)
            .theme(SettingsActivity.THEME == R.style.Theme_Tandera_Claro ? Theme.LIGHT : Theme.DARK)
            .negativeText(getActivity().getString(R.string.close)).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AnimUtils.hideImageCircular(dialogView, dialog);
                }
                }
            })
            .build();

        localMaterialDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface paramDialogInterface) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AnimUtils.revealImageCircular(dialogView);
                return;
            }
            dialogView.setVisibility(View.VISIBLE);
            }
        });localMaterialDialog.show();


        dialogView.setSummary(tvShow.getOverview());
        dialogView.getCoverImageView().loadPoster(tvShow);
        dialogView.setYear(String.valueOf(tvShow.getReleaseDate()));
        dialogView.setRating(tvShow.getAverageRatingPercent() + "%");
        dialogView.getLikeButton().setOnClickListener(v -> {

        });
        dialogView.getShareButton().setOnClickListener(v -> {
        if (getDisplay() != null) {
            getDisplay().shareTvShow(tvShow.getTmdbId(), tvShow.getTitle());
        }
        });
    }

    @Override
    public void onClick(View view, int position) {
        ShowWrapper item = mAdapter.getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }

}