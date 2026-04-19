package com.example.appifood_movil.data.model

// Esta clase solo guarda datos, por eso va en 'model'
data class ReceiptItem(
    val name: String,
    val quantity: Int,
    val totalPrice: String
)