package com.mari05lim.tandera.model.tasks;

import android.util.Log;

import com.google.gson.Gson;
import com.mari05lim.tandera.model.entities.TmdbConfiguration;
import com.mari05lim.tandera.model.network.NetworkCallRunnable;
import com.mari05lim.tandera.model.state.ApplicationState;
import com.mari05lim.tandera.model.util.FileManager;
import com.uwetrottmann.tmdb.Tmdb;
import com.uwetrottmann.tmdb.entities.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * DEV Mari05liM
 */
public class FetchConfigurationRunnable extends NetworkCallRunnable<TmdbConfiguration> {

    private static final String LOG_TAG = FetchConfigurationRunnable.class.getSimpleName();

    private static final String FILENAME_CONFIG = "tmdb.config";

    @Inject
    Tmdb mTmdbClient;
    @Inject
    ApplicationState mState;
    @Inject
    FileManager mFileManager;

    @Override
    public TmdbConfiguration doBackgroundCall() throws RetrofitError {

        TmdbConfiguration configuration = getConfigFromFile();

        if (configuration != null && configuration.isValid()) {
            Log.d(LOG_TAG, "Get config from file");
        } else {
            Log.d(LOG_TAG, "Fetch config from network");
            // No config in file, so download from web
            Configuration tmdbConfig = mTmdbClient.configurationService().configuration();

            if (tmdbConfig != null) {
                System.out.println("Tmdb config !=  null");
                // Downloaded config from web so file it to file
                configuration = new TmdbConfiguration();
                configuration.set(tmdbConfig);
                writeConfigToFile(configuration);
            } else {
                System.out.println("Tmdb config ==  null");
                configuration = null;
            }
        }

        return configuration;
    }

    @Override
    public void onSuccess(TmdbConfiguration result) {
        if (result != null) {
          //  mImageHelper.setTmdbBaseUrl(result.getImagesBaseUrl());
         //   System.out.println("TMDB cofig: " + result.getImagesBaseUrl() + "  " + result.getImagesBackdropSizes());
          //  mImageHelper.setTmdbBackdropSizes(result.getImagesBackdropSizes());
          //  mImageHelper.setTmdbPosterSizes(result.getImagesPosterSizes());
          //  mImageHelper.setTmdbProfileSizes(result.getImagesProfileSizes());
        }

        mState.setTmdbConfiguration(result);
    }

    @Override
    public void onError(RetrofitError re) {
        System.out.println("On error");

    }

    private TmdbConfiguration getConfigFromFile() {
        System.out.println("Read from file");
        File file = mFileManager.getFile(FILENAME_CONFIG);
        if (file.exists()) {
            System.out.println("Config file exists");
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                Gson gson = new Gson();
                return gson.fromJson(reader, TmdbConfiguration.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private void writeConfigToFile(TmdbConfiguration configuration) {
        System.out.println("Write to file");
        FileWriter writer = null;

        try {
            File file = mFileManager.getFile(FILENAME_CONFIG);
            if (!file.exists()) {
                System.out.println("Config file doesnt exists");
                file.createNewFile();
            }

            writer = new FileWriter(file, false);
            Gson gson = new Gson();
            gson.toJson(configuration, writer);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}