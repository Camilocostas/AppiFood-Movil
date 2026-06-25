// ui/viewmodel/CartViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.ReceiptItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    // ui/viewmodel/CartViewModel.kt
// Solo cambia la forma de construir los ReceiptItem —
// ahora el último parámetro se llama initialQuantity

    val cartItems = mutableStateListOf(
        ReceiptItem(id = 1, name = "Delicious Burger",    price = 35000, imageRes = R.drawable.cheese,         initialQuantity = 1),
        ReceiptItem(id = 2, name = "Chicago Deep Pizza",  price = 44000, imageRes = R.drawable.chicago_pizza,  initialQuantity = 2),
        ReceiptItem(id = 3, name = "Coca Cola 1L",        price =  6000, imageRes = R.drawable.cocacola,       initialQuantity = 1),
        ReceiptItem(id = 4, name = "Ramen Tonkotsu",      price = 22000, imageRes = R.drawable.ramen,          initialQuantity = 3),
        ReceiptItem(id = 5, name = "Mega-Taco",           price = 23000, imageRes = R.drawable.cheese,         initialQuantity = 2),
        ReceiptItem(id = 6, name = "Sopa de guisantes",   price = 16000, imageRes = R.drawable.lomosaltado,    initialQuantity = 1),
        ReceiptItem(id = 7, name = "Helado de Yogurt",    price =  9000, imageRes = R.drawable.helado,         initialQuantity = 1),
        ReceiptItem(id = 8, name = "Pastel de Chocolate", price =  8000, imageRes = R.drawable.ensaladacesar,  initialQuantity = 4)
    )

    // ── Derivados reactivos: se recalculan solo cuando cartItems cambia ──
    val subtotal by derivedStateOf { cartItems.sumOf { it.price * it.quantity } }
    val shipping = 3_500
    val total    by derivedStateOf { subtotal + shipping }

    // ── Estado del cupón ──────────────────────────────────────────
    var couponCode   = mutableStateOf("")
    var couponApplied = mutableStateOf(false)
    var couponError  = mutableStateOf<String?>(null)

    // ── Estados de UI para flujo de pago ─────────────────────────
    var showPaySheet    = mutableStateOf(false)
    var showReceipt     = mutableStateOf(false)
    var showConfirmAnim = mutableStateOf(false)
    var selectedPayment = mutableStateOf("Efectivo")
    var paymentDetail   = mutableStateOf("")

    // ── Operaciones del carrito ───────────────────────────────────
    fun increaseQuantity(item: ReceiptItem) {
        item.quantity++
    }

    fun decreaseQuantity(item: ReceiptItem) {
        if (item.quantity > 1) item.quantity--
        else removeItem(item)
    }

    fun removeItem(item: ReceiptItem) {
        cartItems.remove(item)
    }

    // ── Vaciar carrito desde el ViewModel (no desde la UI) ───────
    fun clearCart() {
        cartItems.clear()
        couponApplied.value = false
        couponCode.value    = ""
        couponError.value   = null
    }

    // ── Validación de cupón (stub — conectar con API real) ────────
    fun applyCoupon() {
        couponError.value = when (couponCode.value.trim().uppercase()) {
            "BIENVENIDO" -> { couponApplied.value = true; null }
            ""           -> "Ingresa un código de cupón"
            else         -> "Cupón no válido"
        }
    }

    fun formatCurrency(amount: Int): String =
        "\$${String.format("%,d", amount).replace(",", ".")}"

    // ── Confirmar pedido ──────────────────────────────────────────
    fun confirmOrder() {
        showConfirmAnim.value = true
    }

    fun onOrderAnimationFinished() {
        showConfirmAnim.value = false
        showReceipt.value     = true
    }
}