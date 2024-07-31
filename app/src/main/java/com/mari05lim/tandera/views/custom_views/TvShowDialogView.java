package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class TvShowDialogView extends RelativeLayout {

    private final ImageButton mLikeButton;
    private final ImageButton mShareButton;
    private final TextView mYearTextView;
    private final TextView mRatingTextView;
    private final TanderaImageView mCoverImageView;
    private final TextView mSummaryTextView;

    public TvShowDialogView(Context context) {
        this(context, null);
    }

    public TvShowDialogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvShowDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.item_show_dialog, this, true);

        mLikeButton = findViewById(R.id.like_button);
        mShareButton = findViewById(R.id.share_button);

        mYearTextView = findViewById(R.id.year_text_view);
        mRatingTextView = findViewById(R.id.rating_text_view);

        mCoverImageView = findViewById(R.id.show_cover_image);
        mSummaryTextView = findViewById(R.id.summary_text_view);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvShowDialogView);
        a.recycle();
    }

    public void setYear(CharSequence text) {
        mYearTextView.setText(text);
    }

    public void setRating(CharSequence text) {
        mRatingTextView.setText(text);
    }

    public void setSummary(CharSequence text) {
        mSummaryTextView.setText(text);
    }

    public TextView getYearTextView() {
        return mYearTextView;
    }

    public TextView getRatingTextView() {
        return mRatingTextView;
    }

    public TanderaImageView getCoverImageView() {
        return mCoverImageView;
    }

    public TextView getSummaryTextView() {
        return mSummaryTextView;
    }

    public ImageButton getLikeButton() {
        return mLikeButton;
    }

    public ImageButton getShareButton() {
        return mShareButton;
    }

    /*
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mRatingTextView.setCompoundDrawablesWithIntrinsicBounds(
                new BitmapDrawable(getResources(), bitmap),
                null, null, null);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        mRatingTextView.setCompoundDrawables(null, null, null, null);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        mRatingTextView.setCompoundDrawables(null, null, null, null);
    }
    */

}