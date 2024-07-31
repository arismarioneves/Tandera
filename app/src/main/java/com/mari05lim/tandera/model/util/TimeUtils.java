package com.mari05lim.tandera.model.util;

/**
 * DEV Mari05liM
 */
public class TimeUtils {

    public static boolean isBeforeStartingPoint(final long time, final long point) {
        return isInPast(time - point);
    }

    public static boolean isAfterStartingPoint(final long time, final long point) {
        return isInFuture(time - point);
    }

    public static boolean isPastStartingPoint(final long time, final long point) {
        return isInPast(time + point);
    }

    public static boolean isInPast(final long time) {
        return time <= System.currentTimeMillis();
    }

    public static boolean isInFuture(final long time) {
        return time > System.currentTimeMillis();
    }

}