package com.mari05lim.tandera.ui.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mari05lim.tandera.TanderaApplication

/** Fábrica de ViewModel com acesso às dependências da aplicação e ao SavedStateHandle (argumentos da rota). */
inline fun <reified VM : ViewModel> factory(
    crossinline create: (app: TanderaApplication, handle: SavedStateHandle) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer { create(this[APPLICATION_KEY] as TanderaApplication, createSavedStateHandle()) }
}
