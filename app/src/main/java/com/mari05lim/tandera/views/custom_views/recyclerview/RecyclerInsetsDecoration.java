package com.mari05lim.tandera.views.custom_views.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.mvp.views.UiView;

/**
 * DEV Mari05liM
 */
public class RecyclerInsetsDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;

    public RecyclerInsetsDecoration(Context context, UiView.NavigationGridType type) {
        switch (type) {
            case MOVIES:
                mSpacing = context.getResources().getDimensionPixelSize(R.dimen.movie_grid_spacing);
                break;
            case SHOWS:
                mSpacing = context.getResources().getDimensionPixelSize(R.dimen.show_grid_spacing);
                break;
            case WATCHED:
                mSpacing = context.getResources().getDimensionPixelSize(R.dimen.show_grid_spacing);
                break;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mSpacing, mSpacing, mSpacing, mSpacing);
    }

}