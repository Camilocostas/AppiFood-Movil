package com.example.appifood_movil.domain.model

data class ReceiptItem(
    val name: String,
    var quantity: Int,
    val price: Int,
    val imageRes: Int
)
