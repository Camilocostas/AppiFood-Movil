// domain/model/Restaurant.kt
package com.example.appifood_movil.domain.model

data class Restaurant(
    val id       : Int    = 0,      // ← nuevo: necesario para navegación
    val name     : String = "",
    val phone    : String = "",     // ← nuevo: para el comprobante
    val address  : String = "",
    val imageRes : Int    = 0,
    val imageUrl : String = "",
    val schedule : String = "",
    val hasDelivery  : Boolean = false,
    val rating       : String  = "4.5",
    val category     : String  = "",
    val deliveryTime : String  = "20-30 min",
    val dishes   : List<Dish>   = emptyList(),
    val latitude : Double = 0.0,
    val longitude: Double = 0.0,
    val reviews  : List<Review> = emptyList()
)

data class Dish(
    val name     : String = "",
    val price    : Double = 0.0,
    val imageRes : Int    = 0
)

data class Review(
    val user    : String = "",
    val comment : String = "",
    val rating  : Int    = 5
)