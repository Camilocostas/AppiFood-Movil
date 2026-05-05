package com.example.appifood_movil.domain.model

data class Restaurant(
    val name: String,
    val address: String,
    val imageRes: Int,
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
    val imageRes: Int
)

data class Review(
    val user: String,
    val comment: String,
    val rating: Int
)
