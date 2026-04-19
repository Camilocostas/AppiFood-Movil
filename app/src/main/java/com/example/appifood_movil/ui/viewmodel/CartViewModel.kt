package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.ReceiptItem

class CartViewModel : ViewModel() {
    val cartItems = mutableStateListOf(
        ReceiptItem("Delicious Burger", 1, "$35.000"),
        ReceiptItem("Chicago Deep Pizza", 2, "$44.000")
    )

    var showPaySheet = mutableStateOf(false)
    var showReceipt = mutableStateOf(false)
    var selectedPayment = mutableStateOf("Efectivo")
    var paymentDetail = mutableStateOf("")

    val total: String = "$82.500"
}