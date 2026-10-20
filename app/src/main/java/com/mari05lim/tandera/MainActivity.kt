package com.mari05lim.tandera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mari05lim.tandera.data.AppPrefs
import com.mari05lim.tandera.ui.TanderaRoot
import com.mari05lim.tandera.ui.theme.TanderaTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // null até o DataStore responder: a splash fica na tela para o tema não "piscar".
        val prefs: StateFlow<AppPrefs?> = (application as TanderaApplication).settings.prefs
            .stateIn(lifecycleScope, SharingStarted.Eagerly, null)
        splash.setKeepOnScreenCondition { prefs.value == null }

        setContent {
            val current by prefs.collectAsStateWithLifecycle()
            val ready = current ?: return@setContent
            TanderaTheme(ready) { TanderaRoot() }
        }
    }
}
