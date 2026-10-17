package com.mari05lim.tandera.data

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.mari05lim.tandera.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Tipos aceitos pela API Tandera (parâmetro e campo "tipo"). @Keep: também é argumento de rota. */
@Keep
@Serializable
enum class MediaType(val apiValue: String, @StringRes val label: Int) {
    @SerialName("filme")
    Movie("filme", R.string.type_movie),

    @SerialName("serie")
    Series("serie", R.string.type_series),
}

/** Listas de descoberta (parâmetro "lista"). @Keep: também é argumento de rota. */
@Keep
enum class Catalog(val apiValue: String) {
    Trending("em_alta"),
    NowPlaying("cartaz"),
    Upcoming("em_breve"),
    OnTheAir("no_ar"),
}

/** Item de lista: resultados de busca, listas de descoberta e "Assistidos". */
@Serializable
data class TitleSummary(
    val id: Int,
    @SerialName("tipo") val type: MediaType,
    @SerialName("titulo") val title: String,
    @SerialName("titulo_original") val originalTitle: String? = null,
    @SerialName("ano") val year: Int? = null,
    @SerialName("poster") val posterUrl: String? = null,
    @SerialName("nota") val rating: Double? = null,
)

@Serializable
data class TitleDetails(
    val id: Int,
    @SerialName("tipo") val type: MediaType,
    @SerialName("titulo") val title: String,
    @SerialName("titulo_original") val originalTitle: String? = null,
    @SerialName("ano") val year: Int? = null,
    @SerialName("poster") val posterUrl: String? = null,
    @SerialName("nota") val rating: Double? = null,
    @SerialName("sinopse") val overview: String? = null,
    @SerialName("fundo") val backdropUrl: String? = null,
    @SerialName("generos") val genres: List<String> = emptyList(),
    @SerialName("duracao") val runtimeMinutes: Int? = null,
    @SerialName("temporadas") val seasons: Int? = null,
    @SerialName("trailer") val trailerUrl: String? = null,
    @SerialName("regiao") val region: String,
    @SerialName("stream") val offers: List<StreamOffer> = emptyList(),
    @SerialName("mais_opcoes") val allOptionsUrl: String? = null,
) {
    fun toSummary() = TitleSummary(id, type, title, originalTitle, year, posterUrl, rating)
}

@Serializable
data class StreamOffer(
    val name: String,
    val link: String,
    val logo: String,
    /** Mantido como texto: um valor novo na API não pode quebrar a leitura da resposta. */
    @SerialName("modalidades") val modalities: List<String> = emptyList(),
)

enum class Modality(val apiValue: String, @StringRes val label: Int) {
    Subscription("assinatura", R.string.modality_subscription),
    Free("gratis", R.string.modality_free),
    Ads("anuncios", R.string.modality_ads),
    Rent("aluguel", R.string.modality_rent),
    Buy("compra", R.string.modality_buy),
    ;

    companion object {
        fun fromApi(value: String): Modality? = entries.firstOrNull { it.apiValue == value }
    }
}

@Serializable
data class CatalogPage(
    @SerialName("pagina") val page: Int,
    @SerialName("total_paginas") val totalPages: Int,
    @SerialName("resultados") val results: List<TitleSummary>,
)

@Serializable
data class SearchResults(
    @SerialName("resultados") val results: List<TitleSummary>,
)
