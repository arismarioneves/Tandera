package com.mari05lim.tandera.views.adapters;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;
import com.mari05lim.tandera.views.listeners.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * DEV Mari05liM
 */
public class MoviesGridAdapter extends UltimateGridLayoutAdapter<MovieWrapper, MoviesGridAdapter.MovieGridViewHolder> {

    private final RecyclerItemClickListener mClickListener;

    public MoviesGridAdapter(List<MovieWrapper> list, RecyclerItemClickListener mClickListener) {
        super(list);
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_grid_movie_card;
    }

    @Override
    protected MovieGridViewHolder newViewHolder(View view) {
        return new MovieGridViewHolder(view, true);
    }

    @Override
    public MovieGridViewHolder newFooterHolder(View view) {
        return new MovieGridViewHolder(view, false);
    }

    @Override
    public MovieGridViewHolder newHeaderHolder(View view) {
        return new MovieGridViewHolder(view, false);
    }

    @Override
    protected void bindNormal(final MovieGridViewHolder holder, final MovieWrapper data, final int position) {
        holder.title.setText(data.getTitle());

        if (data.getReleasedTime() > 0) {
            Date DATE = new Date(data.getReleasedTime());
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            holder.subtitle.setText(dateFormat.format(DATE));
        } else {
            holder.subtitle.setText("--");
        }

        holder.poster.setAutoFade(true);
        holder.poster.loadPoster(data);

        holder.container.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.poster.setTag(String.valueOf(position));
                holder.poster.setTransitionName(holder.itemView.getResources().getString(R.string.transition_poster));
            }
            mClickListener.onClick(holder.poster, position);
        });

        holder.contextMenu.setOnClickListener(v -> mClickListener.onPopupMenuClick(holder.contextMenu, position));
    }

    @Override
    protected void withBindHolder(final MovieGridViewHolder holder, MovieWrapper data, final int position) {

    }

    public class MovieGridViewHolder extends UltimateRecyclerviewViewHolder {
        View container;
        TextView title;
        TextView subtitle;
        TanderaImageView poster;
        ImageView contextMenu;
        View bottomContainer;

        public MovieGridViewHolder(View itemView, boolean isItem) {
            super(itemView);

            if (isItem) {
                container = itemView.findViewById(R.id.card_content_holder);
                poster = itemView.findViewById(R.id.imageview_poster);
                title = itemView.findViewById(R.id.title);
                subtitle = itemView.findViewById(R.id.textview_subtitle_1);
                bottomContainer = itemView.findViewById(R.id.bottom_container);
                contextMenu = itemView.findViewById(R.id.context_menu);
            }
        }
    }

}