package com.mari05lim.tandera.model.util;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.PersonCreditWrapper;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.entities.SeasonWrapper;
import com.mari05lim.tandera.model.entities.ShowWrapper;

import java.net.URLEncoder;


public class ImageHelper {

    private static final boolean RESIZE_ALL = false;

    private  static final String mTmdbBaseUrl = "http://image.tmdb.org/t/p/";
    private  static  final int[] mTmdbPosterSizes = {92, 154, 185, 342, 500, 780};
    private  static  final int[] mTmdbBackdropSizes = {300, 780, 1280};
    private  static  final int[] mTmdbProfileSizes = {45, 185};


    public static String getPosterUrl(final PersonCreditWrapper credit, final int width, final int height) {
        final String imageUrl = credit.getPosterPath();
        Preconditions.checkNotNull(imageUrl, "movie must have poster url");
        String url = buildTmdbPosterUrl(imageUrl, width, RESIZE_ALL);
        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getPosterUrl(final MovieWrapper movie, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(movie.getPosterUrl())) {
            url = buildTmdbPosterUrl(movie.getPosterUrl(),
                    width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getPosterUrl(final ShowWrapper show, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(show.getPosterUrl())) {
            url = buildTmdbPosterUrl(show.getPosterUrl(),
                    width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getPosterUrl(final SeasonWrapper season, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(season.getPosterUrl())) {
            url = buildTmdbPosterUrl(season.getPosterUrl(),
                    width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getFanartUrl(final MovieWrapper movie, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(movie.getBackdropUrl())) {
            url = buildTmdbBackdropUrl(movie.getBackdropUrl(), width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getFanartUrl(final ShowWrapper show, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(show.getBackdropUrl())) {
            url = buildTmdbBackdropUrl(show.getBackdropUrl(), width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getFanartUrl(final MovieWrapper.BackdropImage image,
                               final int width, final int height) {

        final String imageUrl = image.url;
        Preconditions.checkNotNull(imageUrl, "image must have backdrop url");

        String url = null;
        url = buildTmdbBackdropUrl(imageUrl, width, RESIZE_ALL);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }


    public static String getProfileUrl(final PersonWrapper person, final int width, final int height) {
        final String imageUrl = person.getPictureUrl();
        Preconditions.checkNotNull(imageUrl, "movie must have picture url");
        String url = null;
        url = buildTmdbBackdropUrl(imageUrl, width, RESIZE_ALL);
        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getResizedUrl(String url, int width, int height) {
        StringBuilder sb = new StringBuilder("https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy");
        sb.append("?container=focus");
        sb.append("&resize_w=").append(width);
        sb.append("&resize_h=").append(height);
        sb.append("&url=").append(URLEncoder.encode(url));
        sb.append("&refresh=31536000");
        return sb.toString();
    }

    private static String buildTmdbPosterUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbPosterSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbPosterSizes, forceLarger));
        } else {
            return null;
        }
    }

    private static String buildTmdbBackdropUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbBackdropSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbBackdropSizes, forceLarger));
        } else {
            return null;
        }
    }

    private  String buildTmdbProfileUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbProfileSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbProfileSizes, forceLarger));
        } else {
            return null;
        }
    }

    private static int selectSize(final int width, final int[] widths, final boolean forceLarger) {
        int previousBucketWidth = 0;

        for (int i = 0; i < widths.length; i++) {
            final int currentBucketWidth = widths[i];

            if (width < currentBucketWidth) {
                if (forceLarger || previousBucketWidth != 0) {
                    // We're in between this and the previous bucket
                    final int bucketDiff = currentBucketWidth - previousBucketWidth;
                    if (width < previousBucketWidth + (bucketDiff / 2)) {
                        return previousBucketWidth;
                    } else {
                        return currentBucketWidth;
                    }
                } else {
                    return currentBucketWidth;
                }
            } else if (i == widths.length - 1) {
                // If we get here then we're larger than a bucket
                if (width < currentBucketWidth * 2) {
                    return currentBucketWidth;
                }
            }

            previousBucketWidth = currentBucketWidth;
        }
        return Integer.MAX_VALUE;
    }


    private static String buildTmdbUrl(String baseUrl, String imagePath, int width) {
        StringBuilder url = new StringBuilder(baseUrl);
        if (width == Integer.MAX_VALUE) {
            url.append("original");
        } else {
            url.append('w').append(width);
        }
        url.append(imagePath);
        return url.toString();
    }

}