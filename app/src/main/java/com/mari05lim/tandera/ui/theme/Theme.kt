package com.mari05lim.tandera.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.mari05lim.tandera.data.Accent
import com.mari05lim.tandera.data.AppPrefs
import com.mari05lim.tandera.data.ThemeMode

/** Base neutra (a mesma do site): fundo quase branco/quase preto, tingido só pela cor de destaque. */
private val Light = Color(0xFFFAFAFA)
private val Ink = Color(0xFF18181B)
private val Night = Color(0xFF0C0C0E)
private val NightInk = Color(0xFFF4F4F5)

val dynamicColorAvailable: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
fun TanderaTheme(prefs: AppPrefs, content: @Composable () -> Unit) {
    val dark = when (prefs.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val scheme = if (prefs.accent == Accent.DYNAMIC && dynamicColorAvailable) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        schemeFor(Color(prefs.accent.argb), dark)
    }

    // Ícones das barras do sistema acompanham o tema do app, que pode diferir do sistema.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            WindowCompat.getInsetsController((view.context as Activity).window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

fun schemeFor(seed: Color, dark: Boolean): ColorScheme = if (!dark) {
    val container = lerp(seed, Light, 0.82f)
    lightColorScheme(
        primary = seed,
        onPrimary = Color.White,
        primaryContainer = container,
        onPrimaryContainer = lerp(seed, Color.Black, 0.55f),
        secondary = Color(0xFF52525B),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE4E4E7),
        onSecondaryContainer = Ink,
        tertiary = lerp(seed, Ink, 0.25f),
        background = Light,
        onBackground = Ink,
        surface = Light,
        onSurface = Ink,
        surfaceVariant = Color(0xFFE4E4E7),
        onSurfaceVariant = Color(0xFF71717A),
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color(0xFFF4F4F5),
        surfaceContainer = Color(0xFFF4F4F5),
        surfaceContainerHigh = Color(0xFFEDEDEF),
        surfaceContainerHighest = Color(0xFFE4E4E7),
        outline = Color(0xFFD4D4D8),
        outlineVariant = Color(0xFFE4E4E7),
    )
} else {
    val primary = lerp(seed, Color.White, 0.15f)
    darkColorScheme(
        primary = primary,
        onPrimary = Color.White,
        primaryContainer = lerp(seed, Night, 0.6f),
        onPrimaryContainer = lerp(seed, Color.White, 0.8f),
        secondary = Color(0xFFA1A1AA),
        onSecondary = Ink,
        secondaryContainer = Color(0xFF27272A),
        onSecondaryContainer = NightInk,
        tertiary = lerp(primary, NightInk, 0.25f),
        background = Night,
        onBackground = NightInk,
        surface = Night,
        onSurface = NightInk,
        surfaceVariant = Color(0xFF27272A),
        onSurfaceVariant = Color(0xFFA1A1AA),
        surfaceContainerLowest = Color(0xFF09090B),
        surfaceContainerLow = Color(0xFF131316),
        surfaceContainer = Color(0xFF17171A),
        surfaceContainerHigh = Color(0xFF1F1F23),
        surfaceContainerHighest = Color(0xFF27272A),
        outline = Color(0xFF3F3F46),
        outlineVariant = Color(0xFF27272A),
    )
}
