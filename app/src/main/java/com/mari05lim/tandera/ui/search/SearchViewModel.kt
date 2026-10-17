package com.mari05lim.tandera.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mari05lim.tandera.data.MediaType
import com.mari05lim.tandera.data.TanderaApi
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.ui.common.UiState
import com.mari05lim.tandera.ui.common.factory
import com.mari05lim.tandera.ui.common.load
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** null = ainda não buscou (campo vazio). */
typealias SearchState = UiState<List<TitleSummary>>?

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(handle: SavedStateHandle, private val api: TanderaApi) : ViewModel() {

    /** Sobrevivem à morte do processo para a busca reaparecer igual. */
    val query: StateFlow<String> = handle.getStateFlow(KEY_QUERY, "")
    val type: StateFlow<MediaType?> = handle.getStateFlow(KEY_TYPE, null)

    private val savedState = handle
    private val retries = MutableStateFlow(0)

    val results: StateFlow<SearchState> = combine(
        query.map { it.trim() }.distinctUntilChanged().debounce { if (it.length < MIN_LENGTH) 0 else DEBOUNCE_MS },
        type,
        retries,
    ) { q, t, _ -> q to t }
        .flatMapLatest { (q, t) ->
            if (q.length < MIN_LENGTH) flowOf<SearchState>(null)
            else flow<SearchState> {
                emit(UiState.Loading)
                emit(load { api.search(q, t).results })
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onQueryChange(value: String) {
        savedState[KEY_QUERY] = value
    }

    fun onTypeChange(value: MediaType?) {
        savedState[KEY_TYPE] = value
    }

    fun retry() {
        retries.value++
    }

    companion object {
        private const val KEY_QUERY = "query"
        private const val KEY_TYPE = "type"
        private const val MIN_LENGTH = 2
        private const val DEBOUNCE_MS = 350L

        val Factory = factory { app, handle -> SearchViewModel(handle, app.api) }
    }
}
