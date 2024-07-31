package com.mari05lim.tandera.model.util;

import com.mari05lim.tandera.model.network.BackgroundCallRunnable;
import com.mari05lim.tandera.model.network.NetworkCallRunnable;

/**
 * DEV Mari05liM
 */
public interface BackgroundExecutor {
    <R> void execute(NetworkCallRunnable<R> runnable);
    <R> void execute(BackgroundCallRunnable<R> runnable);

}