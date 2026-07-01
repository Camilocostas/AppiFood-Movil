// ui/viewmodel/RestaurantOrderViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.Order
import com.example.appifood_movil.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.collectLatest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RestaurantOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<Order>>(emptyList())
    val filteredOrders: StateFlow<List<Order>> = _filteredOrders.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

    init {
        loadOrdersRealtime()
    }

    private fun loadOrdersRealtime() {
        viewModelScope.launch {
            orderRepository.getOrdersRealtime().collectLatest { orders ->
                _orders.value = orders
                _pendingCount.value = orders.count { it.status == "pending" }
                applyFilter()
            }
        }
    }

    fun filterByStatus(status: String?) {
        _selectedStatus.value = status
        applyFilter()
    }

    private fun applyFilter() {
        val status = _selectedStatus.value
        _filteredOrders.value = if (status == null) {
            _orders.value
        } else {
            _orders.value.filter { it.status == status }
        }
    }
    // En RestaurantOrderViewModel.kt

    suspend fun saveNotificationToProfile(userId: String, title: String, message: String) {
        try {
            val notification = mapOf(
                "title" to title,
                "message" to message,
                "timestamp" to System.currentTimeMillis(),
                "read" to false,
                "type" to "order_status"
            )
            // Guardar en Firestore en la subcolección del usuario
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("notifications")
                .add(notification)
                .await()
            android.util.Log.d("OrderVM", "✅ Notificación guardada para usuario: $userId")
        } catch (e: Exception) {
            android.util.Log.e("OrderVM", "❌ Error guardando notificación: ${e.message}")
        }
    }
    suspend fun loadOrderDetail(orderId: String) {
        _isLoading.value = true
        try {
            val order = orderRepository.getOrderById(orderId)
            _selectedOrder.value = order
        } catch (e: Exception) {
            _error.value = "Error al cargar el pedido: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            _isLoading.value = true
            val result = orderRepository.updateOrderStatus(orderId, status)
            if (result) {
                // Actualizar la lista local
                val updatedOrders = _orders.value.map { order ->
                    if (order.orderId == orderId) {
                        order.copy(status = status)
                    } else {
                        order
                    }
                }
                _orders.value = updatedOrders
                _pendingCount.value = updatedOrders.count { it.status == "pending" }
                applyFilter()

                // Actualizar el pedido seleccionado
                _selectedOrder.value = _selectedOrder.value?.copy(status = status)
            }
            _isLoading.value = false
            result
        } catch (e: Exception) {
            _error.value = "Error al actualizar el estado: ${e.message}"
            _isLoading.value = false
            false
        }
    }

    fun clearSelectedOrder() {
        _selectedOrder.value = null
    }

    fun getStatusColor(status: String): Long {
        return when (status) {
            "pending" -> 0xFFFF9800 // Naranja
            "preparing" -> 0xFF2196F3 // Azul
            "ready" -> 0xFF4CAF50 // Verde
            "delivered" -> 0xFF9E9E9E // Gris
            "cancelled" -> 0xFFD32F2F // Rojo
            else -> 0xFF000000
        }
    }

    fun getStatusLabel(status: String): String {
        return when (status) {
            "pending" -> "Pendiente"
            "preparing" -> "En preparación"
            "ready" -> "Listo"
            "delivered" -> "Entregado"
            "cancelled" -> "Cancelado"
            else -> status
        }
    }
}