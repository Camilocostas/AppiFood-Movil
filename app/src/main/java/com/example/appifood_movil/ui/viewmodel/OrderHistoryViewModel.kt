// ui/viewmodel/OrderHistoryViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth      = FirebaseAuth.getInstance()

    private val _orders    = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error     = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { loadOrders() }

    fun loadOrders() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        _error.value     = null

        firestore.collection("orders")
            // ── Filtra por UID del cliente logueado ───────────────
            // Cada Order guardado tiene customer.uid — solo carga
            // los pedidos del usuario actual.
            .whereEqualTo("customer.uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val orderList = mutableListOf<Order>()
                for (doc in documents) {
                    try {
                        val order = doc.toObject(Order::class.java)
                        orderList.add(order)
                    } catch (e: Exception) {
                        Log.e("OrderHistoryVM", "Error parseando pedido ${doc.id}", e)
                    }
                }
                _orders.value    = orderList
                _isLoading.value = false
                Log.d("OrderHistoryVM", "Pedidos cargados: ${orderList.size}")
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _error.value     = e.message ?: "Error al cargar pedidos"
                Log.e("OrderHistoryVM", "Error cargando historial", e)
            }
    }

    fun formatCurrency(amount: Int): String =
        "\$${String.format("%,d", amount).replace(",", ".")}"

    fun formatDate(timestamp: Long): String {
        if (timestamp == 0L) return "Fecha desconocida"
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("es", "CO"))
        return sdf.format(java.util.Date(timestamp))
    }
}