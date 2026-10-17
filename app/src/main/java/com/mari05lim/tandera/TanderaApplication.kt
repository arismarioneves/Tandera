package com.mari05lim.tandera

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.mari05lim.tandera.data.LegacyWatchedImport
import com.mari05lim.tandera.data.SettingsStore
import com.mari05lim.tandera.data.TanderaApi
import com.mari05lim.tandera.data.TanderaJson
import com.mari05lim.tandera.data.WatchedStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "tandera")

/** Dependências do app, criadas uma vez (sem framework de DI: o grafo é pequeno). */
class TanderaApplication : Application(), SingletonImageLoader.Factory {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** Um só cliente para API e imagens: compartilha conexões e o cache HTTP em disco. */
    private val httpClient by lazy {
        OkHttpClient.Builder()
            .cache(Cache(cacheDir.resolve("http"), 50L * 1024 * 1024))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    val api by lazy { TanderaApi(httpClient, BuildConfig.API_URL.toHttpUrl(), BuildConfig.API_KEY, TanderaJson) }
    val watched by lazy { WatchedStore(dataStore, TanderaJson) }
    val settings by lazy { SettingsStore(dataStore) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch { LegacyWatchedImport(this@TanderaApplication, watched).run() }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { httpClient })) }
            .crossfade(true)
            .build()
}
