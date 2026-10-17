package com.mari05lim.tandera.ui.common

import com.mari05lim.tandera.data.ApiException

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Failure(val message: String) : UiState<Nothing>
}

/** Executa [block] convertendo falhas da API em [UiState.Failure]; cancelamento continua propagando. */
suspend fun <T> load(block: suspend () -> T): UiState<T> = try {
    UiState.Success(block())
} catch (e: ApiException) {
    UiState.Failure(e.message.orEmpty())
}
