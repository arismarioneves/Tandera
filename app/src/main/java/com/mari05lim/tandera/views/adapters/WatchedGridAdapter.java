package com.mari05lim.tandera.views.adapters;

import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;
import com.mari05lim.tandera.views.custom_views.TanderaTextView;
import com.mari05lim.tandera.views.listeners.RecyclerItemClickListener;
import com.mari05lim.tandera.model.entities.Watchable;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class WatchedGridAdapter extends UltimateGridLayoutAdapter<Watchable, WatchedGridAdapter.WatchedItemViewHolder> {

    private final RecyclerItemClickListener mClickListener;

    public WatchedGridAdapter(List<Watchable> list, RecyclerItemClickListener mClickListener) {
        super(list);
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_watched_list;
    }

    @Override
    protected WatchedItemViewHolder newViewHolder(View view) {
        return new WatchedItemViewHolder(view, true);
    }

    @Override
    public WatchedItemViewHolder newHeaderHolder(View view) {
        return new WatchedItemViewHolder(view, false);
    }

    @Override
    protected void bindNormal(WatchedItemViewHolder holder, Watchable data, int position) {
        holder.title.setText(data.getTitle());
        holder.watchedType.setText(data.getWatchableType().getResId());
        holder.poster.loadPoster(data);
    }

    @Override
    protected void withBindHolder(WatchedItemViewHolder holder, Watchable data, int position) {
    }

    public class WatchedItemViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        View container;
        TanderaTextView title;
        TanderaTextView watchedType;
        TanderaImageView poster;

        public WatchedItemViewHolder(View itemView, boolean isItem) {
            super(itemView);

            if (isItem) {
                container = itemView.findViewById(R.id.container);
                container.setOnClickListener(this);
                poster = itemView.findViewById(R.id.imageview_poster);
                title = itemView.findViewById(R.id.title);
                watchedType = itemView.findViewById(R.id.subtitle);
            }
        }

        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            if (viewId == R.id.container) {
                mClickListener.onClick(poster, getPosition());
            }
        }
    }

}