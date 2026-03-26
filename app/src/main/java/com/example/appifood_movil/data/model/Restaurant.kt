package com.example.appifood_movil.data.model

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double,
    val deliveryTime: String,
    val category: String
)
