package com.mari05lim.tandera.model.entities;

import com.mari05lim.tandera.R;

/**
 * DEV Mari05liM
 */
public enum WatchableType {
    MOVIE(R.string.watchable_movie),
    TV_SHOW(R.string.watchable_show),
    TV_SEASON(R.string.watchable_season),
    TV_EPISODE(R.string.watchable_episode),
    NONE(R.string.watchable_none);

    private final Integer resId;

    WatchableType(Integer resource) {
        this.resId = resource;
    }

    public Integer getResId() {
        return this.resId;
    }

}