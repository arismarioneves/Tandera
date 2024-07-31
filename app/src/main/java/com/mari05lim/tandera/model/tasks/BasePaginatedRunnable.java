package com.mari05lim.tandera.model.tasks;

import com.mari05lim.tandera.model.state.BaseState;

import java.util.ArrayList;

/**
 * DEV Mari05liM
 */
abstract class BasePaginatedRunnable<R extends BaseState.PaginatedResult<T>, T, MR> extends BaseRunnable<MR> {

    private final int mPage;

    public BasePaginatedRunnable(int callingId, int mPage) {
        super(callingId);
        this.mPage = mPage;
    }


    @Override
    public void onSuccess(MR result) {
        if (result != null) {
            R paginatedResult = getResultFromState();

            if (paginatedResult == null) {
                paginatedResult = createPaginatedResult();
                paginatedResult.items = new ArrayList<>();
            }

            updatePaginatedResult(paginatedResult, result);
            updateState(paginatedResult);
        }
    }


    protected int getPage() {
        return mPage;
    }

    protected abstract void updatePaginatedResult(R result, MR tmdbResult);

    protected abstract R getResultFromState();

    protected abstract R createPaginatedResult();

    protected abstract void updateState(R result);

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        if (mPage > 1) {
            return new BaseState.ShowLoadingProgressEvent(getCallingId(), show, true);
        } else {
            return super.createLoadingProgressEvent(show);
        }
    }

}