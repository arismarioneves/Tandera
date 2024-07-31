package com.mari05lim.tandera.model.util;

import java.util.Collection;

/**
 * DEV Mari05liM
 */
public class MoviesCollections {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static int size(Collection<?> collection) {
        return collection != null ? collection.size() : 0;
    }

}