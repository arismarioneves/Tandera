package com.mari05lim.tandera.views.fragments.base;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.util.MoviesCollections;
import com.mari05lim.tandera.mvp.views.ListMoviesView;
import com.mari05lim.tandera.util.TanderaPreferences;
import com.mari05lim.tandera.views.adapters.MoviesGridAdapter;
import com.mari05lim.tandera.views.custom_views.recyclerview.AutofitGridLayoutManager;
import com.mari05lim.tandera.views.custom_views.recyclerview.RecyclerInsetsDecoration;
import com.mari05lim.tandera.views.listeners.MovieMenuItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;

import java.util.List;

/**
 * DEV Mari05liM
 */
public abstract class MoviesGridFragment extends BaseGridFragment<MoviesGridAdapter.MovieGridViewHolder, MovieWrapper> implements ListMoviesView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int width = (int)getActivity().getResources().getDimension(R.dimen.movie_grid_item_width);
        getRecyclerView().setLayoutManager(new AutofitGridLayoutManager(getActivity(), width , getAdapter()));
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity(), NavigationGridType.MOVIES));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_movie_recycler;
    }

    public TanderaQueryType getQueryType() {
        return TanderaQueryType.COMMON_MOVIES;
    }

    @Override
    protected UltimateGridLayoutAdapter<MovieWrapper, MoviesGridAdapter.MovieGridViewHolder> createAdapter(List<MovieWrapper> data) {
        return new MoviesGridAdapter(data, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                onRefreshData(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view, int position) {
       MovieWrapper item = getAdapter().getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void onPopupMenuClick(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.movie_popup_menu);

        MovieWrapper movieWrapper = mAdapter.getObjects().get(position);

        // show/hide some menu items depending on movie information
        Menu menu = popupMenu.getMenu();

        menu.findItem(R.id.menu_action_web_search)
                .setVisible(movieWrapper.getTitle( ) != null);

        menu.findItem(R.id.menu_action_trailer)
                .setVisible(movieWrapper != null && !MoviesCollections.isEmpty(movieWrapper.getTrailers()));

        popupMenu.setOnMenuItemClickListener(
                new MovieMenuItemClickListener(movieWrapper,getDisplay()));
        popupMenu.show();
    }

    @Override
    public void showItemDetail(MovieWrapper movie,View view){
        Preconditions.checkNotNull(movie, "movie cannot be null");
        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && TanderaPreferences.areAnimationsEnabled(getContext())
                        && view.getTag() != null) {
                    display.startMovieDetailActivityBySharedElements(String.valueOf(movie.getTmdbId()), view, (String) view.getTag());
                } else {
                    display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), null);
                }
            }
        }
    }

}