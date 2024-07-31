package com.mari05lim.tandera.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.util.CountryProvider;

import java.util.Locale;

import static com.mari05lim.tandera.model.Constants.mLanguageCode_PtBR;
import static com.mari05lim.tandera.model.Constants.mLanguageCode_EnUS;
import static com.mari05lim.tandera.model.Constants.mCountryCode_PtBR;
import static com.mari05lim.tandera.model.Constants.mCountryCode_EnUS;

/**
 * DEV Mari05liM
 */
public class TanderaCountryProvider implements CountryProvider {

    private final Context mContext;

    public TanderaCountryProvider(Context mContext) {
        this.mContext = Preconditions.checkNotNull(mContext, "context can not be null");
    }

    @Override
    public String getLetterCountryCode() {
//        String code;
//        if (mCountryCode == null) {
//            code = getLetterCountryCodeFromLocale();
//            mCountryCode = code;
//            }


        String mCountryCode = "";

        int country = TanderaPreferences.getApplicationContry(mContext);
        switch (country) {
            case 0:
                mCountryCode = mCountryCode_PtBR;
                break;
            case 1:
                mCountryCode = mCountryCode_EnUS;
                break;
        }
        return  mCountryCode;
        }

    private String getLetterCountryCodeFromLocale() {
        final Locale locale = Locale.getDefault();

        final String countryCode = locale.getCountry();
        if (!TextUtils.isEmpty(countryCode)) {
            return countryCode;
        }

        return null;
    }

    @Override
    public String getLetterLanguageCode() {
//       if (mLanguageCode == null) {
//           Locale locale = Locale.getDefault();
//           mLanguageCode = locale.getLanguage();
//       }

        String mLanguageCode = "";

        int country = TanderaPreferences.getApplicationContry(mContext);
        switch (country) {
            case 0:
                mLanguageCode = mLanguageCode_PtBR;
                break;
            case 1:
                mLanguageCode = mLanguageCode_EnUS;
                break;
        }

        return  mLanguageCode;
    }

}