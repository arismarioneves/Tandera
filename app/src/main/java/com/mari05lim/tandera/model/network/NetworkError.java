package com.mari05lim.tandera.model.network;

import retrofit.RetrofitError;
import retrofit.client.Response;

public enum NetworkError {

    NOT_FOUND_TMDB, NETWORK_ERROR, UNKNOWN;

    public static NetworkError from(final RetrofitError error) {
        if (error == null) {
            return UNKNOWN;
        }

        final Response response = error.getResponse();

        if (response == null) {
            return UNKNOWN;
        }

        if (error.isNetworkError()) {
            return NETWORK_ERROR;
        }

        final int statusCode = response.getStatus();

         if (statusCode == 404) {
             return NOT_FOUND_TMDB;

        }
        return UNKNOWN;
    }

}