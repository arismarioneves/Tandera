package com.mari05lim.tandera.util;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.util.StringFetcher;

/**
 * DEV Mari05liM
 */
public class AndroidStringFetcher implements StringFetcher {

    private final Context mContext;

    public AndroidStringFetcher(Context context) {
        mContext = Preconditions.checkNotNull(context, "context cannot be null");
    }

    @Override
    public String getString(int id) {
        return mContext.getString(id);
    }

    @Override
    public String getString(int id, Object... format) {
        return mContext.getString(id, format);
    }

}