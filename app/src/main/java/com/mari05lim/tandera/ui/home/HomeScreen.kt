package com.mari05lim.tandera.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.Catalog
import com.mari05lim.tandera.data.MediaType
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.ui.common.PosterCard
import com.mari05lim.tandera.ui.common.PosterPlaceholder
import com.mari05lim.tandera.ui.common.UiState

private val CardWidth = 120.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTitleClick: (TitleSummary) -> Unit,
    onSeeAll: (Catalog, MediaType) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val sections by viewModel.sections.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TopAppBar(title = { Brand() }, scrollBehavior = scrollBehavior) },
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            HomeSection.entries.forEach { section ->
                item(key = section.name) {
                    SectionRow(
                        section = section,
                        state = sections.getValue(section),
                        onTitleClick = onTitleClick,
                        onSeeAll = { onSeeAll(section.catalog, section.type) },
                        onRetry = { viewModel.retry(section) },
                    )
                }
            }
        }
    }
}

/** "tandera." com o ponto vermelho, como no site. */
@Composable
private fun Brand() {
    Text(
        buildAnnotatedString {
            append("tandera")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append(".") }
        },
        fontWeight = FontWeight.ExtraBold,
    )
}

@Composable
private fun SectionRow(
    section: HomeSection,
    state: UiState<List<TitleSummary>>,
    onTitleClick: (TitleSummary) -> Unit,
    onSeeAll: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(section.title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onSeeAll, enabled = state is UiState.Success) { Text(stringResource(R.string.action_see_all)) }
        }

        val rowPadding = PaddingValues(horizontal = 16.dp)
        val spacing = Arrangement.spacedBy(12.dp)
        when (state) {
            UiState.Loading -> LazyRow(contentPadding = rowPadding, horizontalArrangement = spacing, userScrollEnabled = false) {
                items(5) { PosterPlaceholder(CardWidth) }
            }

            is UiState.Failure -> Row(
                Modifier
                    .fillMaxWidth()
                    .height(CardWidth)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    state.message,
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
            }

            is UiState.Success -> LazyRow(contentPadding = rowPadding, horizontalArrangement = spacing) {
                items(state.data, key = { it.id }) { item ->
                    PosterCard(item, onClick = { onTitleClick(item) }, modifier = Modifier.width(CardWidth))
                }
            }
        }
    }
}
