package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.ReceiptItem
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.FoodProduct

class CartViewModel : ViewModel() {
    // En CartViewModel.kt
    val cartItems = mutableStateListOf(
        // Agrega el parámetro de la imagen (debe coincidir con el orden del constructor de ReceiptItem)
        ReceiptItem("Delicious Burger", 1, 35000, R.drawable.cheese),
        ReceiptItem("Chicago Deep Pizza", 2, 44000, R.drawable.chicago_pizza),
        ReceiptItem( "Coca Cola 1L", 1,6000, R.drawable.cocacola),
        ReceiptItem( "Ramen Tonkotsu", 3, 22000, R.drawable.ramen),
        ReceiptItem("Mega-Taco", 2,23000, R.drawable.cheese),
        ReceiptItem( "Sopa de guisantes", 1,16000, R.drawable.lomosaltado),
        ReceiptItem( "Helado de Yogurt", 1,9000, R.drawable.helado),
        ReceiptItem( "Pastel de Chocolate", 4,8000, R.drawable.ensaladacesar)
    )
    var totalAmount by mutableIntStateOf(123000)
    var showPaySheet = mutableStateOf(false)
    var showReceipt = mutableStateOf(false)
    var selectedPayment = mutableStateOf("Efectivo")
    var paymentDetail = mutableStateOf("")

    fun formatCurrency(amount: Int): String {
        return "$${String.format("%,d", amount).replace(",", ".")}"
    }

    fun updateTotals() {
        totalAmount = cartItems.sumOf { it.price * it.quantity }
    }

    fun increaseQuantity(item: ReceiptItem) {
        item.quantity++
        updateTotals()
    }

    fun decreaseQuantity(item: ReceiptItem) {
        if (item.quantity > 1) {
            item.quantity--
            updateTotals()
        }
    }
}