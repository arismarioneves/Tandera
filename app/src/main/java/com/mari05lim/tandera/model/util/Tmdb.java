package com.mari05lim.tandera.model.util;

/**
 * DEV Mari05liM
 */
public class Tmdb {

    /**
     * Show status used when exporting data. Compare with {@link com.mari05lim.tandera.model.entities.ShowWrapper}.
     */
    public interface ShowStatusExport {
        String CONTINUING = "Returning Series";
        String ENDED = "Ended";
        String UNKNOWN = "Unknown";
    }

}