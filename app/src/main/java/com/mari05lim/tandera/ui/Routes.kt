package com.mari05lim.tandera.ui

import com.mari05lim.tandera.data.Catalog
import com.mari05lim.tandera.data.MediaType
import kotlinx.serialization.Serializable

@Serializable data object HomeRoute
@Serializable data object SearchRoute
@Serializable data object WatchedRoute
@Serializable data object SettingsRoute

@Serializable data class DetailsRoute(val type: MediaType, val id: Int)

@Serializable data class CatalogRoute(val catalog: Catalog, val type: MediaType)
