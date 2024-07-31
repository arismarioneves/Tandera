package com.mari05lim.tandera.views.adapters;

import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;
import com.mari05lim.tandera.views.custom_views.TanderaTextView;
import com.mari05lim.tandera.views.listeners.RecyclerItemClickListener;
import com.mari05lim.tandera.model.entities.PersonWrapper;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class PeopleListAdapter extends easyRegularAdapter<PersonWrapper, PeopleListAdapter.PeopleListViewHolder> {

    private final RecyclerItemClickListener mClickListener;

    public PeopleListAdapter(List<PersonWrapper> list, RecyclerItemClickListener mClickListener) {
        super(list);
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_list_2line;
    }

    @Override
    protected PeopleListViewHolder newViewHolder(View view) {
        return new PeopleListViewHolder(view, true);
    }

    @Override
    public PeopleListViewHolder newHeaderHolder(View view) {
        return new PeopleListViewHolder(view, false);
    }

    @Override
    protected void withBindHolder(PeopleListViewHolder holder, PersonWrapper data, int position) {

        holder.name.setText(data.getName());
        holder.poster.setAvatarMode(true);
        holder.poster.loadProfile(data);
    }

    public class PeopleListViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        View container;
        TanderaTextView name;
        TanderaImageView poster;

        public PeopleListViewHolder(View itemView, boolean isItem) {
            super(itemView);

            if (isItem) {
                container = itemView.findViewById(R.id.container);
                container.setOnClickListener(this);
                poster = itemView.findViewById(R.id.imageview_poster);
                name = itemView.findViewById(R.id.title);
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