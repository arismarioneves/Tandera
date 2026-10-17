package com.mari05lim.tandera.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mari05lim.tandera.data.Region
import com.mari05lim.tandera.data.SettingsStore
import com.mari05lim.tandera.data.TanderaApi
import com.mari05lim.tandera.data.TitleDetails
import com.mari05lim.tandera.data.WatchedStore
import com.mari05lim.tandera.ui.DetailsRoute
import com.mari05lim.tandera.ui.common.UiState
import com.mari05lim.tandera.ui.common.factory
import com.mari05lim.tandera.ui.common.load
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(
    handle: SavedStateHandle,
    private val api: TanderaApi,
    private val settings: SettingsStore,
    private val watchedStore: WatchedStore,
) : ViewModel() {

    private val route = handle.toRoute<DetailsRoute>()

    private val _details = MutableStateFlow<UiState<TitleDetails>>(UiState.Loading)
    val details: StateFlow<UiState<TitleDetails>> = _details.asStateFlow()

    val watched: StateFlow<Boolean> = watchedStore.isWatched(route.type, route.id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val region: StateFlow<Region> = settings.region
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Region.Brazil)

    init {
        // A disponibilidade depende do país: recarrega se ele mudar com a tela aberta.
        viewModelScope.launch { settings.region.collectLatest { fetch(it) } }
    }

    fun retry() {
        viewModelScope.launch { fetch(settings.region.first()) }
    }

    fun toggleWatched() {
        val loaded = (_details.value as? UiState.Success)?.data ?: return
        viewModelScope.launch { watchedStore.toggle(loaded.toSummary()) }
    }

    private suspend fun fetch(region: Region) {
        _details.value = UiState.Loading
        _details.value = load { api.details(route.type, route.id, region.code) }
    }

    companion object {
        val Factory = factory { app, handle -> DetailsViewModel(handle, app.api, app.settings, app.watched) }
    }
}
