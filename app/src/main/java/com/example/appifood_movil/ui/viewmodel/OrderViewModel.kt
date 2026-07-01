// ui/viewmodel/OrderViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.CustomerInfo
import com.example.appifood_movil.data.model.Order
import com.example.appifood_movil.data.model.OrderItem
import com.example.appifood_movil.data.model.PaymentInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

// ✅ Importar el RestaurantInfo del viewmodel
import com.example.appifood_movil.ui.viewmodel.RestaurantInfo as ViewModelRestaurantInfo

@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth      = FirebaseAuth.getInstance()

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder.asStateFlow()

    private val _savedOrder = MutableStateFlow<Order?>(null)
    val savedOrder: StateFlow<Order?> = _savedOrder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun buildOrder(
        userData        : com.example.appifood_movil.data.model.UserData?,
        restaurantInfo  : ViewModelRestaurantInfo,
        cartItems       : List<com.example.appifood_movil.data.model.ReceiptItem>,
        deliveryAddress : String,
        paymentMethod   : String,
        paymentDetail   : String,
        shipping        : Int
    ) {
        val uid      = auth.currentUser?.uid ?: ""
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val total    = subtotal + shipping

        val order = Order(
            orderId         = UUID.randomUUID().toString().take(8).uppercase(),
            timestamp       = System.currentTimeMillis(),
            status          = "pending",
            customer        = CustomerInfo(
                uid = uid,
                fullName = "${userData?.names ?: ""} ${userData?.lastNames ?: ""}".trim(),
                phone = userData?.phone ?: ""
            ),
            restaurant      = ViewModelRestaurantInfoToData(restaurantInfo),
            items           = cartItems.map { item ->
                OrderItem(
                    name = item.name,
                    quantity = item.quantity,
                    price = item.price,
                    subtotal = item.price * item.quantity
                )
            },
            payment         = PaymentInfo(method = paymentMethod, detail = paymentDetail),
            deliveryAddress = deliveryAddress,
            subtotal        = subtotal,
            shipping        = shipping,
            total           = total
        )
        _currentOrder.value = order
    }

    // ✅ Función de conversión con los campos en español
    private fun ViewModelRestaurantInfoToData(viewModelInfo: ViewModelRestaurantInfo): com.example.appifood_movil.data.model.RestaurantInfo {
        return com.example.appifood_movil.data.model.RestaurantInfo(
            nombre = viewModelInfo.nombre,
            descripcion = viewModelInfo.descripcion,
            categoria = viewModelInfo.categoria,
            direccion = viewModelInfo.direccion,
            telefono = viewModelInfo.telefono,
            horario = viewModelInfo.horario,
            imagenPortada = viewModelInfo.imagenPortada,
            fotosGaleria = viewModelInfo.fotosGaleria
        )
    }

    fun confirmAndSaveOrder(onSuccess: (String) -> Unit) {
        val order = _currentOrder.value ?: return
        _isLoading.value = true

        firestore.collection("orders")
            .document(order.orderId)
            .set(order)
            .addOnSuccessListener {
                _isLoading.value  = false
                _savedOrder.value = order
                _error.value      = null
                Log.d("OrderViewModel", "Pedido guardado: ${order.orderId}")
                onSuccess(order.orderId)
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _error.value     = e.message ?: "Error al guardar el pedido"
                Log.e("OrderViewModel", "Error guardando pedido", e)
            }
    }

    fun loadOrderById(orderId: String) {
        _isLoading.value = true
        firestore.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                _isLoading.value = false
                if (document.exists()) {
                    val order = document.toObject(Order::class.java)
                    _savedOrder.value = order
                } else {
                    _error.value = "Pedido no encontrado"
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _error.value = e.message ?: "Error al cargar el pedido"
                Log.e("OrderViewModel", "Error cargando pedido $orderId", e)
            }
    }

    fun clearOrder() {
        _currentOrder.value = null
        _savedOrder.value   = null
        _error.value        = null
    }

    fun formatCurrency(amount: Int): String =
        "\$${String.format("%,d", amount).replace(",", ".")}"

    fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}