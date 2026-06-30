package com.example.appifood_movil.domain.model

import com.example.appifood_movil.R

data class Restaurant(
    val id: Int = 0,
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val imageRes: Int = R.drawable.restaurantechino,
    val imageUrl: String = "",
    val schedule: String = "",
    val hasDelivery: Boolean = false,
    val rating: String = "4.5",
    val category: String = "",
    val description: String = "",          // 🆕 NUEVO — para buscar por palabras clave
    val deliveryTime: String = "20-30 min",
    val dishes: List<Dish> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val reviews: List<Review> = emptyList(),
    val uid: String = "",
    val estado: String = "activo",
    val fotosGaleria: List<String> = emptyList()
)

data class Dish(
    val name: String = "",
    val price: Double = 0.0,
    val imageRes: Int = R.drawable.cheese,
    val imageUrl: String = ""
)

data class Review(
    val user: String = "",
    val comment: String = "",
    val rating: Int = 5
)