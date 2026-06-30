package com.example.appifood_movil.domain.model

import com.example.appifood_movil.R

data class Restaurant(
    val id: Int = 0,
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val imageRes: Int = R.drawable.restaurantechino,  // ← FALLBACK
    val imageUrl: String = "",                        // ← PRIORIDAD
    val schedule: String = "",
    val hasDelivery: Boolean = false,
    val rating: String = "4.5",
    val category: String = "",
    val deliveryTime: String = "20-30 min",
    val dishes: List<Dish> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val reviews: List<Review> = emptyList(),
    val uid: String = "",
    val estado: String = "activo"
)

data class Dish(
    val name: String = "",
    val price: Double = 0.0,
    val imageRes: Int = R.drawable.cheese,  // ← FALLBACK
    val imageUrl: String = ""               // ← PRIORIDAD
)

data class Review(
    val user: String = "",
    val comment: String = "",
    val rating: Int = 5
)