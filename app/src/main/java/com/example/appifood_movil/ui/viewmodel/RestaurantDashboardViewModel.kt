// ui/viewmodel/RestaurantDashboardViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.Review
import com.example.appifood_movil.data.repository.ReviewRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.flow.asStateFlow
@HiltViewModel
class RestaurantDashboardViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,          // ✅ Coma agregada
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    // ── Platos activos ──────────────────────────────────────────────
    private val _platosActivos = MutableStateFlow(0)
    val platosActivos: StateFlow<Int> = _platosActivos

    // ── Pedidos de hoy ──────────────────────────────────────────────
    private val _pedidosHoy = MutableStateFlow(0)
    val pedidosHoy: StateFlow<Int> = _pedidosHoy

    private val _restaurantName = MutableStateFlow("")
    val restaurantName: StateFlow<String> = _restaurantName.asStateFlow()

    fun loadRestaurantName(restauranteId: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection("restaurants").document(restauranteId).get().await()
                val name = doc.getString("restaurantName") ?: doc.getString("nombre") ?: ""
                _restaurantName.value = name
            } catch (e: Exception) {
                Log.e("Dashboard", "Error cargando nombre: ${e.message}")
            }
        }
    }

    private val _ingresosHoy = MutableStateFlow(0.0)
    val ingresosHoy: StateFlow<Double> = _ingresosHoy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ── Reseñas ──────────────────────────────────────────────────────
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _averageRating = MutableStateFlow(0.0)
    val averageRating: StateFlow<Double> = _averageRating

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> = _reviewCount

    // ── Cargar platos activos ──────────────────────────────────────
    fun loadPlatosActivos(restauranteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("restaurants")
                    .document(restauranteId)
                    .get()
                    .await()

                if (doc.exists()) {
                    val dishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()
                    val activos = dishes.count {
                        it["disponible"] as? Boolean ?: true
                    }
                    _platosActivos.value = activos
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Cargar pedidos de hoy ──────────────────────────────────────
    fun loadPedidosHoy(restauranteId: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection("restaurants")
                    .document(restauranteId)
                    .get()
                    .await()

                val nombreRestaurante = doc.getString("restaurantName") ?: ""

                if (nombreRestaurante.isEmpty()) {
                    _pedidosHoy.value = 0
                    _ingresosHoy.value = 0.0
                    return@launch
                }

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val inicioDelDia = calendar.timeInMillis

                val querySnapshot = firestore.collection("orders")
                    .whereEqualTo("restaurant.nombre", nombreRestaurante)
                    .whereGreaterThanOrEqualTo("timestamp", inicioDelDia)
                    .get()
                    .await()

                val pedidos = querySnapshot.documents
                    .mapNotNull { it.toObject(com.example.appifood_movil.data.model.Order::class.java) }

                val pedidosDelDia = pedidos.filter {
                    it.status != "cancelled" && it.status != "delivered"
                }
                _pedidosHoy.value = pedidosDelDia.size

                // ✅ Si total es Int, sumOf devuelve Int → convertimos a Double
                val ingresos = pedidosDelDia.sumOf { it.total }
                _ingresosHoy.value = ingresos.toDouble()

            } catch (e: Exception) {
                e.printStackTrace()
                _pedidosHoy.value = 0
                _ingresosHoy.value = 0.0
            }
        }
    }

    // ── Cargar reseñas en tiempo real ──────────────────────────────
    fun loadReviews(restaurantUid: String) {
        viewModelScope.launch {
            reviewRepository.getReviewsForRestaurant(restaurantUid)
                .collect { reviews ->
                    _reviews.value = reviews
                    _reviewCount.value = reviews.size
                    _averageRating.value = if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
                }
        }
    }

    // ── Cargar todo (platos + pedidos + reseñas) ──────────────────
    fun loadDashboardData(restauranteId: String) {
        loadPlatosActivos(restauranteId)
        loadPedidosHoy(restauranteId)
        loadReviews(restauranteId)   // ✅ Incluye reseñas
    }

    // ── Helpers ─────────────────────────────────────────────────────
    fun getPlatosActivosCount(): Int = _platosActivos.value
    fun getPedidosHoyCount(): Int = _pedidosHoy.value
    fun getIngresosHoy(): Double = _ingresosHoy.value
}