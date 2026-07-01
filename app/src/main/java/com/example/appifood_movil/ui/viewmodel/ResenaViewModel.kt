package com.example.appifood_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Resena(
    val id: String = "",
    val usuario: String = "",
    val comentario: String = "",
    val calificacion: Int = 5,
    val respuesta: String = "",
    val fecha: Long = System.currentTimeMillis()
)

@HiltViewModel
class ResenaViewModel @Inject constructor() : ViewModel() {

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadResenas(restauranteId: String) {}

    fun responderResena(restauranteId: String, resenaId: String, respuesta: String) {}
}
