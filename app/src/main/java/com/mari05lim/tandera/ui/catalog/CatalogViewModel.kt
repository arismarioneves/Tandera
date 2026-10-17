package com.mari05lim.tandera.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mari05lim.tandera.data.ApiException
import com.mari05lim.tandera.data.SettingsStore
import com.mari05lim.tandera.data.TanderaApi
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.ui.CatalogRoute
import com.mari05lim.tandera.ui.common.factory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogState(
    val items: List<TitleSummary> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
)

/** Lista completa com carregamento por página ao rolar. */
class CatalogViewModel(
    handle: SavedStateHandle,
    private val api: TanderaApi,
    private val settings: SettingsStore,
) : ViewModel() {

    val route = handle.toRoute<CatalogRoute>()

    private val _state = MutableStateFlow(CatalogState())
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    private var nextPage = 1
    private var job: Job? = null

    init {
        loadMore()
    }

    fun loadMore() {
        val current = _state.value
        if (job?.isActive == true || current.endReached) return

        job = viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val region = settings.region.first().code
                val page = api.catalog(route.catalog, route.type, region, nextPage)
                nextPage = page.page + 1
                _state.update { s ->
                    // O TMDB pode repetir um título entre páginas de listas que mudam ao longo do dia.
                    val known = s.items.mapTo(HashSet()) { it.id }
                    s.copy(
                        items = s.items + page.results.filterNot { it.id in known },
                        loading = false,
                        endReached = page.page >= page.totalPages,
                    )
                }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = factory { app, handle -> CatalogViewModel(handle, app.api, app.settings) }
    }
}
