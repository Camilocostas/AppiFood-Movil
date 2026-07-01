package com.example.appifood_movil.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Application

data class RestaurantInfo(
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val horario: String = "",
    val imagenPortada: String = "",
    val fotosGaleria: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@HiltViewModel
class RestaurantInfoViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress: StateFlow<Int> = _uploadProgress
    private val _restaurantInfo = MutableStateFlow<RestaurantInfo?>(null)
    val restaurantInfo: StateFlow<RestaurantInfo?> = _restaurantInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadRestaurantInfo(restauranteId: String) {}
    fun updateRestaurantInfo(
        restauranteId: String, nombre: String, descripcion: String,
        categoria: String, direccion: String, telefono: String, horario: String
    ) {}
    fun uploadPortadaImage(restauranteId: String, imageUri: Uri) {}
    fun uploadGaleriaImage(restauranteId: String, imageUri: Uri) {}
    fun removeGaleriaImage(restauranteId: String, imageUrl: String) {}
}
