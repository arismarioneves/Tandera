package com.mari05lim.tandera.util;

/**
 * DEV Mari05liM
 */
public class FlagUrlProvider {

    public String getCountryFlagUrl(String countryCode) {
        return "http://www.geonames.org/flags/x/" + countryCode.toLowerCase() + ".gif";
    }

}