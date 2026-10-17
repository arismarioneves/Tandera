package com.mari05lim.tandera.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Falha exibível ao usuário. [message] já vem pronta para a tela. */
class ApiException(message: String) : Exception(message)

/** Campos novos na API não podem quebrar versões antigas do app. */
val TanderaJson = Json { ignoreUnknownKeys = true }

/**
 * Cliente da API Tandera (TanderaWEB/api). A chave do TMDB fica só no servidor; [appKey]
 * identifica o app (X-Tandera-Key) para que limites do site não se apliquem a ele.
 */
class TanderaApi(
    private val client: OkHttpClient,
    private val baseUrl: HttpUrl,
    private val appKey: String,
    private val json: Json,
) {
    suspend fun catalog(catalog: Catalog, type: MediaType, region: String, page: Int): CatalogPage =
        get("lista" to catalog.apiValue, "tipo" to type.apiValue, "regiao" to region, "pagina" to page.toString())

    suspend fun search(query: String, type: MediaType?): SearchResults =
        get("busca" to query, "tipo" to type?.apiValue)

    suspend fun details(type: MediaType, id: Int, region: String): TitleDetails =
        get("id" to id.toString(), "tipo" to type.apiValue, "regiao" to region)

    /** Leitura do corpo e parse rodam fora da main thread; a chamada em si é cancelável. */
    private suspend inline fun <reified T> get(vararg params: Pair<String, String?>): T = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder().apply {
            params.forEach { (name, value) -> if (value != null) addQueryParameter(name, value) }
        }.build()

        val body = try {
            val request = Request.Builder().url(url).apply { if (appKey.isNotEmpty()) header(KEY_HEADER, appKey) }.build()
            client.newCall(request).await().use { response ->
                val text = response.body.string()
                if (!response.isSuccessful) throw ApiException(errorMessage(response.code, text))
                text
            }
        } catch (e: IOException) {
            throw ApiException(NETWORK_ERROR)
        }

        try {
            json.decodeFromString<T>(body)
        } catch (e: IllegalArgumentException) { // SerializationException é subclasse
            throw ApiException(GENERIC_ERROR)
        }
    }

    /** 4xx trazem mensagem pensada para o usuário; 5xx não expõem detalhes. */
    private fun errorMessage(code: Int, body: String): String {
        if (code >= 500) return if (code == 503) UNAVAILABLE_ERROR else GENERIC_ERROR
        return runCatching { json.parseToJsonElement(body).jsonObject["error"]?.jsonPrimitive?.content }
            .getOrNull() ?: GENERIC_ERROR
    }

    private companion object {
        const val KEY_HEADER = "X-Tandera-Key"
        const val NETWORK_ERROR = "Sem conexão. Verifique sua internet e tente novamente."
        const val UNAVAILABLE_ERROR = "Serviço temporariamente indisponível. Tente novamente em instantes."
        const val GENERIC_ERROR = "Não foi possível carregar agora. Tente novamente."
    }
}

/** Executa a chamada sem bloquear a thread e a cancela junto com a corrotina. */
private suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
    enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) = continuation.resume(response) { _, _, _ -> response.close() }
        override fun onFailure(call: Call, e: IOException) = continuation.resumeWithException(e)
    })
    continuation.invokeOnCancellation { cancel() }
}
