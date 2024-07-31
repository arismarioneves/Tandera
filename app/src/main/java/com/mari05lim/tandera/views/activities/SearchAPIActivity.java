package com.mari05lim.tandera.views.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;

import android.util.Log;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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

import static com.mari05lim.tandera.model.Constants.APP_VERSION;
import static com.mari05lim.tandera.model.Constants.API_TANDERA;
import static com.mari05lim.tandera.model.Constants.ENABLE_ADMOB_BANNER_ADS;

/**
 * DEV Mari05liM
 */
public class SearchAPIActivity extends BaseNavigationActivity {

    @Override
    protected void handleIntent(Intent intent, Display display) { }

    @Override
    protected int getContentViewLayoutId() {
        return ENABLE_ADMOB_BANNER_ADS ? R.layout.search_api_activity_ads : R.layout.search_api_activity;
    }

    private ListView listStream;

    private AdView mAdView;

    ProgressBar loadingBar;

    String titleitem;
    int yearitem;
    String typeitem;

    TextView typeTitulo;
    TextView nomeTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

        nomeTitulo = findViewById(R.id.nomeTitulo);
        typeTitulo = findViewById(R.id.typeTitulo);

        listStream = findViewById(R.id.listStream);

        Bundle filmeNome = getIntent().getExtras();
        if (filmeNome != null) {
            titleitem = filmeNome.getString("itemtitle");
            yearitem = filmeNome.getInt("itemyear");
            typeitem = filmeNome.getString("itemtype");
        }

        nomeTitulo.setText(titleitem);
        typeTitulo.setText(typeitem);

        String nome;
        nome = titleitem
                .replace(" ", "%20") // espaço
                .replace("#", "%23") // #realityhigh
                .replace("&", "%26") // Rick & Morty
                .replace("-", "%2D") // MIB: Homens de Preto - Internacional
                .replace(":", "%3A") // WiFi Ralph: Quebrando a Internet
                .replace("'", "%27") // Grey's Anatomy

                .replace("...", "…")
                .replace("…", "%E2%80%A6") // Era uma Vez em... Hollywood

                .replace("°", "º")
                .replace("º", "%C2%BA") // Jogador N° 1

                .replace("ç", "%C3%A7")

                .replace("á", "%C3%A1")
                .replace("Á", "%C3%81")

                .replace("ã", "%C3%A3")
                .replace("é", "%C3%A9")
                .replace("ê", "%C3%AA")
                .replace("í", "%C3%AD")

                .replace("ó", "%C3%B3")
                .replace("Ó", "%C3%93")

                .replace("ô", "%C3%B4")
                .replace("õ", "%C3%B5")

                .replace("ú", "%C3%BA")
                .replace("Ú", "%C3%9A")
        ;
        //ASCII Encoding Reference
        //https://www.w3schools.com/tags/ref_urlencode.asp

        String tipo;
        if(typeitem.equals("Filme")){
            tipo = "mv";
        }else{
            tipo = "tv";
        }

        String pais;
        int country = TanderaPreferences.getApplicationContry(getApplicationContext());
        switch (country) {
            case 0:
                pais = "br"; //Brasil
                break;
            case 1:
                pais = "us"; //Estados Unidos
                break;
            default:
                pais = "br"; //Brasil
        }

        String URL_TO_HIT = API_TANDERA +
                "?titulo=" + nome + //Tomb%20Raider
                "&ano=" + yearitem + //Ano
                "&tipo=" + tipo + //Filme ou Série
                "&pais=" + pais + //br, us

                "&versao=" + APP_VERSION + //2.0
                "&modelo=" + Build.MODEL.replace(" ", "%20") + //ASUS_Z01KD
                "&android=" + Build.VERSION.RELEASE;  //8.0.0

        Log.d("myTag", URL_TO_HIT);

        new JSONTask().execute(URL_TO_HIT);

        //AdMob
        MobileAds.initialize(this, initializationStatus -> {
        });

        if (ENABLE_ADMOB_BANNER_ADS) {
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }
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
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.streaming_row, result);
                listStream.setAdapter(adapter);
                listStream.setOnItemClickListener((parent, view, position, id) -> {
                    StreamingModel streamingModel = result.get(position);
                    Uri uri = Uri.parse(streamingModel.getLink());

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                    // Definindo animações
                    builder.setStartAnimations(SearchAPIActivity.this,
                            R.anim.slide_in_right, 0);
                    builder.setExitAnimations(SearchAPIActivity.this,
                            0, R.anim.slide_out_right);

                    // Definindo a cor da toolbar
                    builder.setToolbarColor(ActivityCompat.getColor(SearchAPIActivity.this, R.color.primary));
                    builder.setSecondaryToolbarColor(ActivityCompat.getColor(SearchAPIActivity.this, R.color.primary_dark));

                    // Setting a custom back button
                    //builder.setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_arrow_back));

                    // Adding a share action
                    builder.addDefaultShareMenuItem();

                    // Adding a share action (with icon)
                    /*String shareLabel = getString(R.string.share); //Nome
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_share); //Icon

                    // Create a PendingIntent to your BroadCastReceiver implementation
                    Intent actionIntent = new Intent(getApplicationContext(), ShareBroadcastReceiver.class);
                    PendingIntent pendingIntent =
                            PendingIntent.getBroadcast(getApplicationContext(), 0, actionIntent, 0);

                    // Set the pendingIntent as the action to be performed when the button is clicked.
                    builder.setActionButton(icon, shareLabel, pendingIntent);*/

                    // Menu
                    //builder.addMenuItem("Menu", pendingIntent);

                    // Title of the webpage
                    //builder.setShowTitle(true);

                    // Abrir janela
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(SearchAPIActivity.this, uri);

                });
            } else {

                //private View mVictim;
                //mVictim = findViewById(R.id.victim);

                //setContentView(R.layout.streaming_empty_state);

                findViewById(R.id.streamFound).setVisibility(View.INVISIBLE);
                findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);

                nomeTitulo = findViewById(R.id.nomeTitulo);
                nomeTitulo.setText(titleitem);

                typeTitulo = findViewById(R.id.typeTitulo);
                typeTitulo.setText(typeitem);
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

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.nameStream = convertView.findViewById(R.id.nameStream);
                holder.typeStream = convertView.findViewById(R.id.typeStream);
                holder.priceStream = convertView.findViewById(R.id.priceStream);
                holder.iconStream = convertView.findViewById(R.id.iconStream);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = convertView.findViewById(R.id.progressBar);

            final ViewHolder finalHolder = holder;

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

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}