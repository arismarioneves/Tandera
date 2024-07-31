package com.mari05lim.tandera.views.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.gson.Gson;
import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.StreamingModel;
import com.mari05lim.tandera.util.TanderaPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mari05lim.tandera.model.Constants.API_TANDERA;

/**
 * DEV Mari05liM
 */
public class StreamActivity extends BaseNavigationActivity {

    @Override
    protected void handleIntent(Intent intent, Display display) { }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.stream_activity;
    }

    private ListView listStream;

    ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        if (getDisplay() != null) {
            getDisplay().showUpNavigation(true);
            getDisplay().setActionBarTitle(this.getResources().getString(R.string.category_stream));
        }

        this.overridePendingTransition(R.anim.slide_in_right, 0);

        loadingBar = findViewById(R.id.spin_kit);
        DoubleBounce styleLoading = new DoubleBounce();
        loadingBar.setIndeterminateDrawable(styleLoading);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        listStream = findViewById(R.id.listStream);

        String URL_TO_HIT = "";

        int country = TanderaPreferences.getApplicationContry(getApplicationContext());
        switch (country) {
            case 0:
                URL_TO_HIT = API_TANDERA + "streamlist_br.json";
                break;
            case 1:
                URL_TO_HIT = API_TANDERA + "streamlist_us.json";
                break;
        }

        new StreamActivity.JSONTask().execute(URL_TO_HIT);
    }

    @Override
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgress(boolean exibir) {
        loadingBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    public class JSONTask extends AsyncTask<String, String, List<StreamingModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected List<StreamingModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("stream");

                List<StreamingModel> streamingModelList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    StreamingModel streamingModel = gson.fromJson(finalObject.toString(), StreamingModel.class);

                    streamingModelList.add(streamingModel);
                }
                return streamingModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<StreamingModel> result) {
            super.onPostExecute(result);
            showProgress(false);
            if (result != null) {
                StreamActivity.MovieAdapter adapter = new StreamActivity.MovieAdapter(getApplicationContext(), R.layout.streaming_row, result);
                listStream.setAdapter(adapter);
                listStream.setOnItemClickListener((parent, view, position, id) -> {
                    StreamingModel streamingModel = result.get(position);
                    Uri uri = Uri.parse(streamingModel.getLink());

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();

                    builder.addDefaultShareMenuItem();

                    builder.setStartAnimations(StreamActivity.this, R.anim.slide_in_right, 0);

                    builder.setToolbarColor(ContextCompat.getColor(StreamActivity.this, R.color.primary));
                    builder.setSecondaryToolbarColor(ContextCompat.getColor(StreamActivity.this, R.color.primary_dark));

                    customTabsIntent.launchUrl(StreamActivity.this, uri);

                });
            } else {
                setContentView(R.layout.stream_empty_state);
                setupActionBar();
                if (getDisplay() != null) {
                    getDisplay().showUpNavigation(true);
                    getDisplay().setActionBarTitle(getResources().getString(R.string.category_stream));
                }
            }
        }
    }

    public class MovieAdapter extends ArrayAdapter {

        private final List<StreamingModel> streamingModelList;
        private final int resource;
        private final LayoutInflater inflater;

        public MovieAdapter(Context context, int resource, List<StreamingModel> objects) {
            super(context, resource, objects);
            streamingModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            StreamActivity.MovieAdapter.ViewHolder holder;

            if (convertView == null) {
                holder = new StreamActivity.MovieAdapter.ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.nameStream = convertView.findViewById(R.id.nameStream);
                holder.typeStream = convertView.findViewById(R.id.typeStream);
                holder.priceStream = convertView.findViewById(R.id.priceStream);
                holder.iconStream = convertView.findViewById(R.id.iconStream);
                convertView.setTag(holder);
            } else {
                holder = (StreamActivity.MovieAdapter.ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = convertView.findViewById(R.id.progressBar);

            final StreamActivity.MovieAdapter.ViewHolder finalHolder = holder;

            holder.nameStream.setText(streamingModelList.get(position).getName());
            holder.typeStream.setText(streamingModelList.get(position).getSale());
            holder.priceStream.setText(streamingModelList.get(position).getCost());

            ImageLoader.getInstance().displayImage(streamingModelList.get(position).getLogo(), holder.iconStream, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    finalHolder.iconStream.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.iconStream.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.iconStream.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.iconStream.setVisibility(View.INVISIBLE);
                }
            });

            return convertView;
        }

        class ViewHolder {
            private TextView nameStream;
            private TextView typeStream;
            private TextView priceStream;
            private ImageView iconStream;
        }
    }

}