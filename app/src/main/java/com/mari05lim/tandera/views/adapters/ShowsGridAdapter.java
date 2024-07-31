package com.mari05lim.tandera.views.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.palette.graphics.Palette;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.entities.ShowWrapper;
import com.mari05lim.tandera.model.util.FileLog;
import com.mari05lim.tandera.mvp.presenters.TvShowPresenter;
import com.mari05lim.tandera.mvp.views.TvShowWatchedView;
import com.mari05lim.tandera.util.AnimUtils;
import com.mari05lim.tandera.views.custom_views.TanderaImageView;
import com.mari05lim.tandera.views.listeners.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import java.util.List;

/**
 * DEV Mari05liM
 */
public class ShowsGridAdapter extends UltimateGridLayoutAdapter<ShowWrapper, ShowsGridAdapter.ShowViewHolder> implements TvShowWatchedView {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = TvShowPresenter.TAG)
    TvShowPresenter mPresenter;

    private MvpDelegate<? extends ShowsGridAdapter> mMvpDelegate;
    private final MvpDelegate<?> mParentDelegate;
    private final String mChildId;

    private final Context context;

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
            mMvpDelegate.setParentDelegate(mParentDelegate, mChildId);
        }
        return mMvpDelegate;
    }

    private final RecyclerItemClickListener mClickListener;

    public ShowsGridAdapter(List<ShowWrapper> list, Context context, MvpDelegate<?> mParentDelegate, RecyclerItemClickListener mClickListener) {
        super(list);
        this.context = context;
        this.mParentDelegate = mParentDelegate;
        mChildId = String.valueOf(0);
        getMvpDelegate().onCreate();

        this.mClickListener = mClickListener;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_grid_show_card;
    }

    @Override
    protected ShowViewHolder newViewHolder(View view) {
        return new ShowViewHolder(view, true);
    }

    @Override
    public ShowViewHolder newFooterHolder(View view) {
        return new ShowViewHolder(view, false);
    }

    @Override
    public ShowViewHolder newHeaderHolder(View view) {
        return new ShowViewHolder(view, false);
    }

    @Override
    protected void bindNormal(final ShowViewHolder holder, final ShowWrapper data, final int position) {
        holder.title.setText(data.getTitle());

        if (data.getNetworks() != null) {
            holder.subtitle.setText(data.getNetworks());
            holder.subtitle.setVisibility(View.VISIBLE);
        } else {
            holder.subtitle.setVisibility(View.GONE);
        }

        holder.poster.loadPoster(data, new TanderaImageView.OnLoadedListener() {
            @Override
            public void onSuccess(TanderaImageView imageView, Bitmap bitmap, String imageUrl) {
                Palette.generateAsync(bitmap, palette -> {
                    Palette.Swatch primary = palette.getVibrantSwatch();

                    if (primary != null) {
                        holder.title.setTextColor(primary.getTitleTextColor());
                        holder.subtitle.setTextColor(primary.getTitleTextColor());

                        AnimUtils.animateViewColor(holder.containerBar, context.getResources().getColor(R.color.tda_red), primary.getRgb());
                    } else {
                        FileLog.d("Palette", "Can`t get Swatch from Palette");
                    }
                });
            }

            @Override
            public void onError(TanderaImageView imageView) {

            }
        });

        holder.container.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.poster.setTag(String.valueOf(position));
                holder.poster.setTransitionName(holder.itemView.getResources().getString(R.string.transition_poster));
            }
            mClickListener.onClick(holder.poster, position);
        });

        updateHeartButton(data, holder, false);
        holder.like.setOnClickListener(v -> {
            updateHeartButton(data, holder, true);
            mPresenter.toggleShowWatched(data, position);
        });
    }

    @Override
    protected void withBindHolder(final ShowViewHolder holder, final ShowWrapper data, final int position) {
    }

    @Override
    public void updateShowWatched(ShowWrapper item, int position) {
       /* if(items != null) {
          items.set(position, item);
            notifyDataSetChanged();
        }*/
    }

    private void updateHeartButton(final ShowWrapper show, final ShowViewHolder holder, boolean animated) {
        if (animated) {
            if (!show.isWatched()) {
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.like, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.like, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.like, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.like.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.start();
            } else {
                holder.like.setImageResource(R.drawable.ic_heart_white);
            }
        } else {
            if (show.isWatched()) {
                holder.like.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.like.setImageResource(R.drawable.ic_heart_white);
            }
        }

    }

    public class ShowViewHolder extends UltimateRecyclerviewViewHolder {
        View container;
        View containerBar;
        TextView title;
        TextView subtitle;
        ImageButton like;
        TanderaImageView poster;

        public ShowViewHolder(View itemView, boolean isItem) {
            super(itemView);

            if (isItem) {
                container = itemView.findViewById(R.id.item_show_container);
                containerBar = itemView.findViewById(R.id.content_bar);
                poster = itemView.findViewById(R.id.imageview_poster);
                title = itemView.findViewById(R.id.title);
                subtitle = itemView.findViewById(R.id.release);
                like = itemView.findViewById(R.id.like_button);
            }
        }
    }

}