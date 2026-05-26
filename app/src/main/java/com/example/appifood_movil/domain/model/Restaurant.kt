package com.example.appifood_movil.domain.model

import com.example.appifood_movil.R

data class Restaurant(
    val id: Int, // Ahora usamos el ID de la base de datos
    val name: String,
    val address: String,
    val imageRes: Int = R.drawable.restaurantechino, // Imagen por defecto
    val imageUrl: String? = null,
    val schedule: String,
    val hasDelivery: Boolean,
    val rating: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val dishes: List<Dish> = emptyList(),
    val reviews: List<Review> = emptyList()
)

data class Dish(
    val name: String,
    val price: Double,
    val imageRes: Int = R.drawable.arrozchaufa
)

data class Review(
    val user: String,
    val comment: String,
    val rating: Int
)
