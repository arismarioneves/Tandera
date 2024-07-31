package com.mari05lim.tandera.util;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * DEV Mari05liM
 */
public final class TanderaServiceUtils {

    private static OkHttpClient cachingHttpClient;

    private static final String API_CACHE = "api-cache";
    public static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    public static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    private static final int MIN_DISK_API_CACHE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int MAX_DISK_API_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    public static synchronized OkHttpClient getCachingOkHttpClient(Context context) {
        if (cachingHttpClient == null) {
            cachingHttpClient = new OkHttpClient();
            cachingHttpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            cachingHttpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            File cacheDir = createApiCacheDir(context);
            cachingHttpClient.setCache(new Cache(cacheDir, calculateApiDiskCacheSize(cacheDir)));
        }
        return cachingHttpClient;
    }

    static File createApiCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), API_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    static long calculateApiDiskCacheSize(File dir) {
        long size = MIN_DISK_API_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                available = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
            } else {
                available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            }
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_API_CACHE_SIZE), MIN_DISK_API_CACHE_SIZE);
    }

}