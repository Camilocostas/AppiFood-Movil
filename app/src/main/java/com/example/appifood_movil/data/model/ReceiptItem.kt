package com.example.appifood_movil.data.model

// Archivo: data/model/ReceiptItem.kt
// Asegúrate de que ReceiptItem tenga 'var quantity' para permitir cambios
data class ReceiptItem(
    val name: String,
    var quantity: Int,
    val price: Int,
    val imageRes: Int
)