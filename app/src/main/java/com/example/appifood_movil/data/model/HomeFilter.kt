package com.example.appifood_movil.data.model

data class HomeFilter(
    val category: String = "Todas",
    val maxPrice: Float = 100000f,
    val minRating: Float = 1f
)