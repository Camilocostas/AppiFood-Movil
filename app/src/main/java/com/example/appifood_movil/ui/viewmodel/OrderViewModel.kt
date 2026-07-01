// ui/viewmodel/OrderViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.api.request.OrderRequest
import com.example.appifood_movil.data.local.TokenManager
import com.example.appifood_movil.data.model.Order
import com.example.appifood_movil.data.model.OrderItem
import com.example.appifood_movil.data.model.PaymentInfo
import com.example.appifood_movil.data.model.CustomerInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

import com.example.appifood_movil.ui.viewmodel.RestaurantInfo as ViewModelRestaurantInfo

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

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
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val total    = subtotal + shipping

        val order = Order(
            orderId         = UUID.randomUUID().toString().take(8).uppercase(),
            timestamp       = System.currentTimeMillis(),
            status          = "pending",
            customer        = CustomerInfo(
                uid = "0",
                fullName = "${userData?.names ?: ""} ${userData?.lastNames ?: ""}".trim(),
                phone = userData?.phone ?: ""
            ),
            restaurant      = com.example.appifood_movil.data.model.RestaurantInfo(
                nombre = restaurantInfo.nombre,
                descripcion = restaurantInfo.descripcion,
                categoria = restaurantInfo.categoria,
                direccion = restaurantInfo.direccion,
                telefono = restaurantInfo.telefono,
                horario = restaurantInfo.horario,
                imagenPortada = restaurantInfo.imagenPortada,
                fotosGaleria = restaurantInfo.fotosGaleria
            ),
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

    fun confirmAndSaveOrder(onSuccess: (String) -> Unit) {
        val order = _currentOrder.value ?: return
        val token = tokenManager.getBearerToken() ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Bridge to Railway API
                val response = apiService.createOrder(token, OrderRequest(
                    restaurant_id = 1,
                    delivery_address = order.deliveryAddress,
                    payment_method = order.payment.method,
                    items = emptyList() 
                ))
                
                if (response.isSuccessful) {
                    _savedOrder.value = order
                    onSuccess(order.orderId)
                } else {
                    _error.value = "Error al confirmar pedido"
                }
            } catch (e: Exception) {
                Log.e("OrderVM", "Error saving order", e)
                _error.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadOrderById(orderId: String) {
        _savedOrder.value = _currentOrder.value
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
