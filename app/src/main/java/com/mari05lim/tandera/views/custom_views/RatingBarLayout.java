package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public class RatingBarLayout extends RelativeLayout {

    private final TextView mRatingValueTextView;
    private final TextView mRatingRangeTextView;
    private final TextView mRatingVotesTextView;
    private final TextView mRatingLabelTextView;

    private String mRatingValue;
    private int mRatingTotalVotes;

    public RatingBarLayout(Context context) {
        this(context, null);
    }

    public RatingBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.include_rating_bar, this);

        mRatingValueTextView = findViewById(R.id.text_view_rating_value);
        mRatingRangeTextView = findViewById(R.id.text_view_rating_range);
        mRatingRangeTextView.setText(Integer.toString(10));
        mRatingVotesTextView = findViewById(R.id.text_view_rating_votes);
        mRatingLabelTextView = findViewById(R.id.text_view_rating_label);
        mRatingLabelTextView.setText(context.getText(R.string.tmdb));
    }

    public void setRatingValue(String value) {
        if (TextUtils.isEmpty(mRatingValueTextView.getText()) ||
                !value.equals(mRatingValue)) {
            mRatingValue = value;
            mRatingValueTextView.setText(mRatingValue);
        }
    }

    public void setRatingRange(Integer range) {
        if (range != null) {
            mRatingRangeTextView.setText("/" + range);
        }

    }

    public void setRatingVotes(int totalVotes) {
        if (TextUtils.isEmpty(mRatingVotesTextView.getText()) ||
                mRatingTotalVotes != totalVotes) {
            mRatingTotalVotes = totalVotes;

            mRatingVotesTextView.setText(getResources().getQuantityString(R.plurals.votes, mRatingTotalVotes, mRatingTotalVotes));
        }
    }

    public void setWhiteTheme() {
        int colorW = getResources().getColor(R.color.tda_white);

        mRatingValueTextView.setTextColor(colorW);
        mRatingRangeTextView.setTextColor(colorW);
        mRatingVotesTextView.setTextColor(colorW);
        mRatingLabelTextView.setTextColor(colorW);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

}