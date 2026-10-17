package com.mari05lim.tandera.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.MediaType
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.ui.common.LoadingView
import com.mari05lim.tandera.ui.common.MessageView
import com.mari05lim.tandera.ui.common.PosterGrid
import com.mari05lim.tandera.ui.common.UiState

@Composable
fun SearchScreen(
    onTitleClick: (TitleSummary) -> Unit,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val type by viewModel.type.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val gridState = rememberLazyGridState()

    // Rolar a lista recolhe o teclado para mostrar mais resultados.
    LaunchedEffect(gridState.isScrollInProgress) { if (gridState.isScrollInProgress) focusManager.clearFocus() }

    // Teclado aberto ao chegar na aba vazia; ao voltar de um título, mantém a lista visível.
    LaunchedEffect(Unit) { if (query.isEmpty()) focusRequester.requestFocus() }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                .focusRequester(focusRequester),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Rounded.Close, stringResource(R.string.action_clear))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        )

        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null to R.string.filter_all, MediaType.Movie to R.string.filter_movies, MediaType.Series to R.string.filter_series)
                .forEach { (value, label) ->
                    FilterChip(selected = type == value, onClick = { viewModel.onTypeChange(value) }, label = { Text(stringResource(label)) })
                }
        }

        when (val state = results) {
            null -> MessageView(
                stringResource(R.string.search_idle_title),
                text = stringResource(R.string.search_idle_text),
                icon = Icons.Rounded.Search,
            )

            UiState.Loading -> LoadingView()
            is UiState.Failure -> MessageView(stringResource(R.string.error_title), text = state.message, onRetry = viewModel::retry)
            is UiState.Success ->
                if (state.data.isEmpty()) {
                    MessageView(
                        stringResource(R.string.search_empty_title),
                        text = stringResource(R.string.search_empty_text),
                        icon = Icons.Rounded.SearchOff,
                    )
                } else {
                    PosterGrid(state.data, onItemClick = onTitleClick, state = gridState, showType = type == null)
                }
        }
    }
}
