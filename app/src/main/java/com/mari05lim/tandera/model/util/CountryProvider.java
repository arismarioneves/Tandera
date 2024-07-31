package com.mari05lim.tandera.model.util;

/**
 * DEV Mari05liM
 */
public interface CountryProvider {

    /**
     * @return ISO 3166-1 country code
     */
    String getLetterCountryCode();

    /**
     * @return ISO 639-1 language code
     */
    String getLetterLanguageCode();

}