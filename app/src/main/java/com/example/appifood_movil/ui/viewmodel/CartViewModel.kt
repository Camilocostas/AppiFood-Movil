package com.example.appifood_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.ReceiptItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _cartItems = MutableStateFlow<List<ReceiptItem>>(emptyList())
    val cartItems: StateFlow<List<ReceiptItem>> = _cartItems.asStateFlow()

    // ── NUEVO: información del restaurante del carrito actual ────
    // El carrito asume un solo restaurante a la vez. Se fija con
    // el primer producto agregado y se limpia junto con clearCart().
    private val _restaurantName  = MutableStateFlow("")
    val restaurantName: StateFlow<String> = _restaurantName.asStateFlow()

    private val _restaurantPhone = MutableStateFlow("")
    val restaurantPhone: StateFlow<String> = _restaurantPhone.asStateFlow()

    // ── Propiedades calculadas ────────────────────────────────────
    val subtotal: Int
        get() = _cartItems.value.sumOf { it.price * it.quantity }

    val shipping: Int
        get() = if (subtotal > 0) 3500 else 0

    val total: Int
        get() = subtotal + shipping

    fun addItem(
        id: Int,
        name: String,
        price: Int,
        quantity: Int,
        imageRes: Int,
        imageUrl: String? = null,
        adiciones: List<String> = emptyList(),
        // ── NUEVO: parámetros opcionales con defaults ─────────────
        restaurantName  : String = "",
        restaurantPhone : String = ""
    ) {
        // Fijar el restaurante solo si el carrito estaba vacío
        // o si aún no tenía restaurante asignado
        if (_cartItems.value.isEmpty() && restaurantName.isNotBlank()) {
            _restaurantName.value  = restaurantName
            _restaurantPhone.value = restaurantPhone
        }

        _cartItems.update { currentList ->
            val newList = currentList.toMutableList()
            val uniqueId = (id.toString() + adiciones.sorted().joinToString()).hashCode()

            val existingItem = newList.find { it.id == uniqueId }
            if (existingItem != null) {
                existingItem.addQuantity(quantity)
            } else {
                newList.add(
                    ReceiptItem(
                        id = uniqueId,
                        name = name,
                        price = price,
                        imageRes = imageRes,
                        imageUrl = imageUrl,
                        adiciones = adiciones,
                        initialQuantity = quantity
                    )
                )
            }
            newList
        }
    }

    fun updateQuantity(id: Int, increment: Boolean) {
        _cartItems.update { list ->
            list.map { item ->
                if (item.id == id) {
                    if (increment) item.increaseQuantity() else item.decreaseQuantity()
                }
                item
            }.filter { it.quantity > 0 }
        }
    }

    fun removeItem(id: Int) {
        _cartItems.update { list -> list.filter { it.id != id } }
        // Si el carrito quedó vacío, limpia también el restaurante
        if (_cartItems.value.isEmpty()) {
            _restaurantName.value  = ""
            _restaurantPhone.value = ""
        }
    }

    fun clearCart() {
        _cartItems.value       = emptyList()
        _restaurantName.value  = ""
        _restaurantPhone.value = ""
    }

    fun formatCurrency(amount: Int): String =
        "\$${String.format("%,d", amount).replace(",", ".")}"
}