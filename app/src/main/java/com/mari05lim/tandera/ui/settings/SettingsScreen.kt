package com.mari05lim.tandera.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mari05lim.tandera.BuildConfig
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.Accent
import com.mari05lim.tandera.data.AppPrefs
import com.mari05lim.tandera.data.Region
import com.mari05lim.tandera.data.SettingsStore
import com.mari05lim.tandera.data.ThemeMode
import com.mari05lim.tandera.ui.common.factory
import com.mari05lim.tandera.ui.common.openStore
import com.mari05lim.tandera.ui.common.sendFeedback
import com.mari05lim.tandera.ui.common.shareApp
import com.mari05lim.tandera.ui.theme.dynamicColorAvailable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val store: SettingsStore) : ViewModel() {
    val prefs: StateFlow<AppPrefs> = store.prefs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppPrefs())

    fun setRegion(region: Region) = launch { store.setRegion(region) }
    fun setThemeMode(mode: ThemeMode) = launch { store.setThemeMode(mode) }
    fun setAccent(accent: Accent) = launch { store.setAccent(accent) }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    companion object {
        val Factory = factory { app, _ -> SettingsViewModel(app.settings) }
    }
}

private val ScreenPadding = 16.dp

@Composable
fun SettingsScreen(
    onOpenSupport: () -> Unit,
    onOpenAbout: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val prefs by viewModel.prefs.collectAsStateWithLifecycle()
    var choosingRegion by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            stringResource(R.string.tab_settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 24.dp, bottom = 8.dp),
        )

        GroupTitle(stringResource(R.string.settings_appearance))
        SettingLabel(stringResource(R.string.settings_theme))
        ThemeSelector(prefs.themeMode, viewModel::setThemeMode)
        SettingLabel(stringResource(R.string.settings_accent))
        AccentSelector(prefs.accent, viewModel::setAccent)

        GroupTitle(stringResource(R.string.settings_content))
        ActionRow(Icons.Outlined.Public, stringResource(R.string.settings_region), prefs.region.displayName) { choosingRegion = true }

        GroupTitle(stringResource(R.string.app_name))
        ActionRow(Icons.Outlined.LocalCafe, stringResource(R.string.settings_support), stringResource(R.string.settings_support_summary), onOpenSupport)
        ActionRow(Icons.Outlined.Share, stringResource(R.string.settings_share), onClick = context::shareApp)
        ActionRow(Icons.Outlined.StarRate, stringResource(R.string.settings_rate), onClick = context::openStore)
        ActionRow(Icons.AutoMirrored.Outlined.Send, stringResource(R.string.settings_feedback), onClick = context::sendFeedback)
        ActionRow(Icons.Outlined.Info, stringResource(R.string.settings_about), onClick = onOpenAbout)

        Text(
            stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(ScreenPadding),
        )
    }

    if (choosingRegion) {
        RegionDialog(
            selected = prefs.region,
            onSelect = {
                viewModel.setRegion(it)
                choosingRegion = false
            },
            onDismiss = { choosingRegion = false },
        )
    }
}

@Composable
private fun GroupTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 24.dp, bottom = 4.dp),
    )
}

@Composable
private fun SettingLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 12.dp, bottom = 8.dp))
}

@Composable
private fun ActionRow(icon: ImageVector, title: String, summary: String? = null, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = summary?.let { { Text(it) } },
        leadingContent = { Icon(icon, contentDescription = null) },
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
    )
}

@Composable
private fun ThemeSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    val options = listOf(
        ThemeMode.SYSTEM to R.string.theme_system,
        ThemeMode.LIGHT to R.string.theme_light,
        ThemeMode.DARK to R.string.theme_dark,
    )
    SingleChoiceSegmentedButtonRow(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPadding),
    ) {
        options.forEachIndexed { index, (mode, label) ->
            SegmentedButton(
                selected = mode == selected,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
            ) { Text(stringResource(label)) }
        }
    }
}

@Composable
private fun AccentSelector(selected: Accent, onSelect: (Accent) -> Unit) {
    val accents = Accent.entries.filter { it != Accent.DYNAMIC || dynamicColorAvailable }
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding),
    ) {
        accents.forEach { accent ->
            val isSelected = accent == selected
            val color = if (accent == Accent.DYNAMIC) {
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest
            } else {
                Color(accent.argb)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .border(
                        width = if (isSelected) 3.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                        shape = CircleShape,
                    )
                    .padding(4.dp)
                    .background(color, CircleShape)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(accent) }),
            ) {
                when {
                    accent == Accent.DYNAMIC -> Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = stringResource(R.string.accent_dynamic),
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp),
                    )
                    isSelected -> Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun RegionDialog(selected: Region, onSelect: (Region) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_region)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.settings_region_help),
                    Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Region.entries.forEach { region ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(selected = region == selected, role = Role.RadioButton) { onSelect(region) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = region == selected, onClick = null)
                        Text(region.displayName, Modifier.padding(start = 12.dp))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) } },
    )
}
