package com.example.appifood_movil.data.model

data class HomeFilter(
    val category: String = "Todas",
    val maxPrice: Float = 100000f,
    val minRating: Float = 1f
)

data class Dish(
    val name: String,
    val price: String,
    val imageRes: Int
)

data class Restaurant(
    val name: String,
    val address: String,
    val imageRes: Int,
    val schedule: String,
    val hasDelivery: Boolean,
    val rating: String = "4.5",
    val category: String,
    val deliveryTime: String = "20-30 min",
    val dishes: List<Dish> = emptyList()
)
