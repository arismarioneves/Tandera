package com.mari05lim.tandera.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.ui.common.LoadingView
import com.mari05lim.tandera.ui.common.MessageView
import com.mari05lim.tandera.ui.common.PosterGrid
import com.mari05lim.tandera.ui.home.HomeSection

/** Quantos itens antes do fim começam a carregar a próxima página. */
private const val PREFETCH_DISTANCE = 9

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onBack: () -> Unit,
    onTitleClick: (TitleSummary) -> Unit,
    viewModel: CatalogViewModel = viewModel(factory = CatalogViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val title = HomeSection.of(viewModel.route.catalog, viewModel.route.type)?.title ?: R.string.app_name

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.items.isEmpty() && state.error != null ->
                MessageView(stringResource(R.string.error_title), Modifier.padding(padding), text = state.error, onRetry = viewModel::loadMore)

            state.items.isEmpty() -> LoadingView(Modifier.padding(padding))

            else -> {
                val gridState = rememberLazyGridState()
                val nearEnd by remember {
                    derivedStateOf {
                        val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        last >= gridState.layoutInfo.totalItemsCount - PREFETCH_DISTANCE
                    }
                }
                LaunchedEffect(nearEnd) { if (nearEnd) viewModel.loadMore() }

                val direction = LocalLayoutDirection.current
                PosterGrid(
                    items = state.items,
                    onItemClick = onTitleClick,
                    state = gridState,
                    contentPadding = PaddingValues(
                        start = padding.calculateStartPadding(direction) + 16.dp,
                        end = padding.calculateEndPadding(direction) + 16.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp,
                    ),
                ) {
                    if (!state.endReached) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                if (state.error != null) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(state.error.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        TextButton(onClick = viewModel::loadMore) { Text(stringResource(R.string.action_retry)) }
                                    }
                                } else {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
