package com.mari05lim.tandera.ui.watched

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.data.WatchedStore
import com.mari05lim.tandera.ui.common.LoadingView
import com.mari05lim.tandera.ui.common.MessageView
import com.mari05lim.tandera.ui.common.PosterGrid
import com.mari05lim.tandera.ui.common.factory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WatchedViewModel(store: WatchedStore) : ViewModel() {
    /** null enquanto o DataStore não respondeu, para não piscar o estado vazio. */
    val items: StateFlow<List<TitleSummary>?> = store.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    companion object {
        val Factory = factory { app, _ -> WatchedViewModel(app.watched) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchedScreen(
    onTitleClick: (TitleSummary) -> Unit,
    viewModel: WatchedViewModel = viewModel(factory = WatchedViewModel.Factory),
) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val count = items?.size ?: 0
                    Text(
                        if (count == 0) stringResource(R.string.tab_watched)
                        else pluralStringResource(R.plurals.watched_title_count, count, count),
                    )
                },
            )
        },
    ) { padding ->
        val list = items
        when {
            list == null -> LoadingView(Modifier.padding(padding))
            list.isEmpty() -> MessageView(
                stringResource(R.string.watched_empty_title),
                Modifier.padding(padding),
                text = stringResource(R.string.watched_empty_text),
                icon = Icons.Rounded.TaskAlt,
            )

            else -> PosterGrid(
                list,
                onItemClick = onTitleClick,
                modifier = Modifier.padding(padding),
                showType = true,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            )
        }
    }
}
