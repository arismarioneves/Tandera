package com.mari05lim.tandera.util;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.util.VisitManager;

/**
 * DEV Mari05liM
 */
public class TanderaVisitManager implements VisitManager {

    private final Context mContext;

    public TanderaVisitManager(Context context) {
        this.mContext = Preconditions.checkNotNull(context, "Context cannot be null");
    }

    @Override
    public boolean isFirstVisitPerformed() {
        return TanderaPreferences.isSetFirstVisitAsPerformed(mContext);
    }

    @Override
    public void recordFirstVisitPerformed() {
        TanderaPreferences.setFirstVisitPerformed(mContext, true);
    }

}