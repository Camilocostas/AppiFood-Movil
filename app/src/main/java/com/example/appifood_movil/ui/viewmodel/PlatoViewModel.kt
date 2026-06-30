package com.example.appifood_movil.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.Adicion
import com.example.appifood_movil.data.repository.Plato
import com.example.appifood_movil.data.repository.PlatoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlatoViewModel @Inject constructor(
    private val platoRepository: PlatoRepository
) : ViewModel() {

    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos: StateFlow<List<Plato>> = _platos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPlatos(restauranteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                platoRepository.getPlatos(restauranteId).collectLatest { platos ->
                    _platos.value = platos
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("PlatoViewModel", "Error cargando platos: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    // ui/viewmodel/PlatoViewModel.kt
    fun updatePlato(
        restauranteId: String,
        platoId: String,
        nombre: String,
        descripcion: String,
        precio: Double,
        adiciones: List<Adicion>,
        imagenUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                val plato = Plato(
                    id = platoId,
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    categoria = "General",
                    adiciones = adiciones,
                    disponible = true,
                    createdAt = System.currentTimeMillis()
                )
                // ✅ Usar savePlato para actualizar (sobrescribe el existente)
                platoRepository.savePlato(restauranteId, plato, imagenUri)
                loadPlatos(restauranteId)
            } catch (e: Exception) {
                Log.e("PlatoViewModel", "Error actualizando plato: ${e.message}")
            }
        }
    }
    fun savePlato(
        restauranteId: String,
        nombre: String,
        descripcion: String,
        precio: Double,
        categoria: String,
        adiciones: List<Adicion>,  // ✅ Recibir adiciones
        imagenUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                val plato = Plato(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    categoria = categoria,
                    adiciones = adiciones,  // ✅ Guardar adiciones
                    disponible = true,
                    createdAt = System.currentTimeMillis()
                )

                platoRepository.savePlato(restauranteId, plato, imagenUri)
                loadPlatos(restauranteId)

            } catch (e: Exception) {
                Log.e("PlatoViewModel", "Error guardando plato: ${e.message}")
            }
        }
    }

    fun toggleDisponible(restauranteId: String, platoId: String) {
        viewModelScope.launch {
            try {
                platoRepository.toggleDisponible(restauranteId, platoId)
                loadPlatos(restauranteId)
            } catch (e: Exception) {
                Log.e("PlatoViewModel", "Error toggling disponible: ${e.message}")
            }
        }
    }

    fun setPromocion(
        restauranteId: String,
        platoId: String,
        precioPromocion: Double,
        descuento: Int
    ) {
        viewModelScope.launch {
            try {
                platoRepository.setPromocion(restauranteId, platoId, precioPromocion, descuento)
                loadPlatos(restauranteId)
            } catch (e: Exception) {
                Log.e("PlatoViewModel", "Error set promocion: ${e.message}")
            }
        }
    }

    fun deletePlato(restauranteId: String, platoId: String) {
        viewModelScope.launch {
            try {
                platoRepository.deletePlato(restauranteId, platoId)
                loadPlatos(restauranteId)
            } catch (e: Exception) {
                Log.e("PlatoViewModel", "Error eliminando plato: ${e.message}")
            }
        }
    }
}