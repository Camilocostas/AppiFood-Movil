// ui/viewmodel/RestaurantDashboardViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.Review
import com.example.appifood_movil.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RestaurantDashboardViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _platosActivos = MutableStateFlow(0)
    val platosActivos: StateFlow<Int> = _platosActivos

    private val _pedidosHoy = MutableStateFlow(0)
    val pedidosHoy: StateFlow<Int> = _pedidosHoy

    private val _restaurantName = MutableStateFlow("")
    val restaurantName: StateFlow<String> = _restaurantName.asStateFlow()

    fun loadRestaurantName(restauranteId: String) {
        // Logic to load from Railway if supported
        _restaurantName.value = "Restaurante Mock"
    }

    private val _ingresosHoy = MutableStateFlow(0.0)
    val ingresosHoy: StateFlow<Double> = _ingresosHoy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _averageRating = MutableStateFlow(0.0)
    val averageRating: StateFlow<Double> = _averageRating

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> = _reviewCount

    fun loadPlatosActivos(restauranteId: String) {}
    fun loadPedidosHoy(restauranteId: String) {}
    fun loadReviews(restaurantUid: String) {}
    fun loadDashboardData(restauranteId: String) {}

    fun getPlatosActivosCount(): Int = _platosActivos.value
    fun getPedidosHoyCount(): Int = _pedidosHoy.value
    fun getIngresosHoy(): Double = _ingresosHoy.value
}
