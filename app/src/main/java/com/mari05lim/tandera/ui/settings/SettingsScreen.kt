package com.mari05lim.tandera.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.mari05lim.tandera.data.Region
import com.mari05lim.tandera.data.SettingsStore
import com.mari05lim.tandera.ui.common.factory
import com.mari05lim.tandera.ui.common.openUrl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val CONTACT_EMAIL = "mariodev@outlook.com.br"

class SettingsViewModel(private val store: SettingsStore) : ViewModel() {
    val region: StateFlow<Region> = store.region.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Region.Brazil)

    fun setRegion(region: Region) {
        viewModelScope.launch { store.setRegion(region) }
    }

    companion object {
        val Factory = factory { app, _ -> SettingsViewModel(app.settings) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)) {
    val region by viewModel.region.collectAsStateWithLifecycle()
    var choosingRegion by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val storeUrl = "https://play.google.com/store/apps/details?id=${context.packageName}"

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_settings)) }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingRow(Icons.Rounded.Public, stringResource(R.string.settings_region), region.displayName) { choosingRegion = true }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SettingRow(Icons.Rounded.StarOutline, stringResource(R.string.settings_rate)) { context.openUrl(storeUrl) }
            SettingRow(Icons.Rounded.MailOutline, stringResource(R.string.settings_feedback), CONTACT_EMAIL) {
                context.openUrl("mailto:$CONTACT_EMAIL?subject=Tandera%20${BuildConfig.VERSION_NAME}")
            }
            SettingRow(Icons.Rounded.Shield, stringResource(R.string.settings_privacy)) {
                context.openUrl("${BuildConfig.SITE_URL}privacidade")
            }
            SettingRow(Icons.AutoMirrored.Rounded.HelpOutline, stringResource(R.string.settings_site), BuildConfig.SITE_URL) {
                context.openUrl(BuildConfig.SITE_URL)
            }
            SettingRow(Icons.Rounded.Info, stringResource(R.string.settings_version), BuildConfig.VERSION_NAME)

            Text(
                stringResource(R.string.attribution_tmdb),
                Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (choosingRegion) {
        RegionDialog(
            selected = region,
            onSelect = {
                viewModel.setRegion(it)
                choosingRegion = false
            },
            onDismiss = { choosingRegion = false },
        )
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String? = null, onClick: (() -> Unit)? = null) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(icon, null) },
        modifier = if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier,
    )
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
