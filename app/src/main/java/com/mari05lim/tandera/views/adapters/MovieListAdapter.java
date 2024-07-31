package com.mari05lim.tandera.views.adapters;

import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;
import com.mari05lim.tandera.views.custom_views.TanderaTextView;
import com.mari05lim.tandera.views.listeners.RecyclerItemClickListener;
import com.mari05lim.tandera.model.entities.MovieWrapper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * DEV Mari05liM
 */
public class MovieListAdapter extends easyRegularAdapter<MovieWrapper, MovieListAdapter.WatchableListViewHolder> {

    private final RecyclerItemClickListener mClickListener;

    private final Date mDate;

    private final DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public MovieListAdapter(List<MovieWrapper> list, RecyclerItemClickListener mClickListener) {
        super(list);
        mDate = new Date();
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_list_3line;
    }

    @Override
    protected WatchableListViewHolder newViewHolder(View view) {
        return new WatchableListViewHolder(view, true);
    }

    @Override
    public WatchableListViewHolder newHeaderHolder(View view) {
        return new WatchableListViewHolder(view, false);
    }

    @Override
    protected void withBindHolder(WatchableListViewHolder holder, MovieWrapper data, int position) {

        if (data.getYear() > 0) {
            holder.title.setText(MoviesApp.get().getAppContext().getString(R.string.movie_title_year, data.getTitle(), data.getYear()));
        } else {
            holder.title.setText(data.getTitle());
        }

        holder.rating.setText(MoviesApp.get().getAppContext().getString(R.string.movie_rating_votes, data.getAverageRatingPercent(), data.getRatingVotes()));

        mDate.setTime(data.getReleasedTime());
        holder.release.setText(MoviesApp.get().getAppContext().getString(R.string.movie_release_date, movieReleaseDate.format(mDate)));

        holder.poster.loadPoster(data);
    }

    public class WatchableListViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        View container;
        TanderaTextView title;
        TanderaTextView rating;
        TanderaTextView release;
        TanderaImageView poster;

        public WatchableListViewHolder(View itemView, boolean isItem) {
            super(itemView);

            if (isItem) {
                container = itemView.findViewById(R.id.container);
                container.setOnClickListener(this);
                poster = itemView.findViewById(R.id.imageview_poster);
                title = itemView.findViewById(R.id.title);
                rating = itemView.findViewById(R.id.textview_subtitle_1);
                release = itemView.findViewById(R.id.textview_subtitle_2);
            }
        }

        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            switch (viewId) {
                case R.id.container :
                    mClickListener.onClick(poster, getPosition());
                    break;
            }
        }
    }

}