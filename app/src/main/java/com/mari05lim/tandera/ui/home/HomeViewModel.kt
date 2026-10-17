package com.mari05lim.tandera.ui.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.Catalog
import com.mari05lim.tandera.data.MediaType
import com.mari05lim.tandera.data.Region
import com.mari05lim.tandera.data.SettingsStore
import com.mari05lim.tandera.data.TanderaApi
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.ui.common.UiState
import com.mari05lim.tandera.ui.common.factory
import com.mari05lim.tandera.ui.common.load
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomeSection(@StringRes val title: Int, val catalog: Catalog, val type: MediaType) {
    TrendingMovies(R.string.section_trending_movies, Catalog.Trending, MediaType.Movie),
    NowPlaying(R.string.section_now_playing, Catalog.NowPlaying, MediaType.Movie),
    TrendingSeries(R.string.section_trending_series, Catalog.Trending, MediaType.Series),
    Upcoming(R.string.section_upcoming, Catalog.Upcoming, MediaType.Movie),
    OnTheAir(R.string.section_on_the_air, Catalog.OnTheAir, MediaType.Series),
    ;

    companion object {
        fun of(catalog: Catalog, type: MediaType): HomeSection? = entries.firstOrNull { it.catalog == catalog && it.type == type }
    }
}

typealias SectionStates = Map<HomeSection, UiState<List<TitleSummary>>>

class HomeViewModel(private val api: TanderaApi, settings: SettingsStore) : ViewModel() {

    private val _sections = MutableStateFlow<SectionStates>(HomeSection.entries.associateWith { UiState.Loading })
    val sections: StateFlow<SectionStates> = _sections.asStateFlow()

    private var region = Region.Brazil

    init {
        // Lançamentos e "no ar" dependem do país: recarrega tudo quando ele muda.
        viewModelScope.launch {
            settings.region.collectLatest { newRegion ->
                region = newRegion
                coroutineScope { HomeSection.entries.forEach { launch { fetch(it) } } }
            }
        }
    }

    fun retry(section: HomeSection) {
        viewModelScope.launch { fetch(section) }
    }

    private suspend fun fetch(section: HomeSection) {
        _sections.update { it + (section to UiState.Loading) }
        val result = load { api.catalog(section.catalog, section.type, region.code, page = 1).results }
        _sections.update { it + (section to result) }
    }

    companion object {
        val Factory = factory { app, _ -> HomeViewModel(app.api, app.settings) }
    }
}
