package com.mari05lim.tandera.ui.details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.foundation.border
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.Modality
import com.mari05lim.tandera.data.Region
import com.mari05lim.tandera.data.StreamOffer
import com.mari05lim.tandera.data.TitleDetails
import com.mari05lim.tandera.ui.common.LoadingView
import com.mari05lim.tandera.ui.common.MessageView
import com.mari05lim.tandera.ui.common.Poster
import com.mari05lim.tandera.ui.common.UiState
import com.mari05lim.tandera.ui.common.openUrl
import com.mari05lim.tandera.ui.common.shareText
import com.mari05lim.tandera.ui.common.siteTitleUrl

@Composable
fun DetailsScreen(onBack: () -> Unit, viewModel: DetailsViewModel = viewModel(factory = DetailsViewModel.Factory)) {
    val state by viewModel.details.collectAsStateWithLifecycle()
    val watched by viewModel.watched.collectAsStateWithLifecycle()
    val region by viewModel.region.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (val s = state) {
            UiState.Loading -> LoadingView()
            is UiState.Failure -> MessageView(stringResource(R.string.error_title), text = s.message, onRetry = viewModel::retry)
            is UiState.Success -> DetailsContent(s.data, watched, region, onToggleWatched = viewModel::toggleWatched)
        }

        // Fica por cima do fundo do título, então usa um círculo translúcido para manter contraste.
        FilledIconButton(
            onClick = onBack,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(8.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.action_back))
        }
    }
}

@Composable
private fun DetailsContent(details: TitleDetails, watched: Boolean, region: Region, onToggleWatched: () -> Unit) {
    val context = LocalContext.current
    val shareMessage = stringResource(R.string.share_text, details.title, siteTitleUrl(details.type, details.id))

    LazyColumn(Modifier.fillMaxSize()) {
        item { Header(details) }
        item {
            Actions(
                details = details,
                watched = watched,
                onToggleWatched = onToggleWatched,
                onTrailer = { details.trailerUrl?.let(context::openUrl) },
                onShare = { context.shareText(shareMessage) },
            )
        }
        item { SectionTitle(stringResource(R.string.details_where_to_watch, region.displayName)) }

        if (details.offers.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.details_no_offers, region.ofName),
                    Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(details.offers, key = { it.name }) { offer ->
                OfferRow(offer, onClick = { context.openUrl(offer.link) })
            }
        }

        details.allOptionsUrl?.let { url ->
            item {
                TextButton(onClick = { context.openUrl(url) }, Modifier.padding(horizontal = 4.dp)) {
                    Text(stringResource(R.string.details_all_options))
                }
            }
        }

        details.overview?.let { overview ->
            item { SectionTitle(stringResource(R.string.details_overview)) }
            item { Overview(overview) }
        }

        item {
            Text(
                stringResource(R.string.attribution_availability),
                Modifier
                    .padding(16.dp)
                    .navigationBarsPadding(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Header(details: TitleDetails) {
    val background = MaterialTheme.colorScheme.background
    Box {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            details.backdropUrl?.let { AsyncImage(it, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(0.4f to Color.Transparent, 1f to background)),
            )
        }

        Row(
            Modifier
                .padding(start = 16.dp, end = 16.dp)
                .padding(top = 140.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Poster(details.posterUrl, details.title, Modifier.width(112.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.offset(y = (-4).dp)) {
                Text(details.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                details.originalTitle?.takeIf { it != details.title }?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(6.dp))
                Text(metaLine(details), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    if (details.genres.isNotEmpty()) {
        FlowRow(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            details.genres.forEach { genre ->
                Text(
                    genre,
                    Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun metaLine(details: TitleDetails): String = listOfNotNull(
    details.year?.toString(),
    stringResource(details.type.label),
    details.runtimeMinutes?.let {
        if (it >= 60) stringResource(R.string.details_runtime_hours, it / 60, it % 60)
        else stringResource(R.string.details_runtime_minutes, it)
    },
    details.seasons?.let { pluralStringResource(R.plurals.details_seasons, it, it) },
    details.rating?.let { "★ %.1f".format(it) },
).joinToString(" · ")

@Composable
private fun Actions(
    details: TitleDetails,
    watched: Boolean,
    onToggleWatched: () -> Unit,
    onTrailer: () -> Unit,
    onShare: () -> Unit,
) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (watched) {
            Button(onClick = onToggleWatched) {
                Icon(Icons.Rounded.Check, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.details_watched))
            }
        } else {
            FilledTonalButton(onClick = onToggleWatched) {
                Icon(Icons.Rounded.Add, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.details_mark_watched))
            }
        }
        if (details.trailerUrl != null) {
            OutlinedButton(onClick = onTrailer) {
                Icon(Icons.Rounded.PlayArrow, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.details_trailer))
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onShare) {
            Icon(Icons.Rounded.Share, stringResource(R.string.action_share))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun OfferRow(offer: StreamOffer, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                offer.logo,
                null,
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(offer.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    // map é inline, então pode chamar stringResource; joinToString não.
                    offer.modalities
                        .map { value -> Modality.fromApi(value)?.let { stringResource(it.label) } ?: value }
                        .joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.OpenInNew,
                stringResource(R.string.details_open_in, offer.name),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Overview(text: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Text(
        text,
        Modifier
            .padding(horizontal = 16.dp)
            .animateContentSize()
            .clickable(role = Role.Button, onClickLabel = stringResource(R.string.action_read_more)) { expanded = !expanded },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = if (expanded) Int.MAX_VALUE else 5,
        overflow = TextOverflow.Ellipsis,
    )
}
