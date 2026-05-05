package com.example.appifood_movil.domain.model

data class FoodProduct(
    val id: Int,
    val name: String,
    val price: Double,
    val imageRes: Int,
    val category: String
)
