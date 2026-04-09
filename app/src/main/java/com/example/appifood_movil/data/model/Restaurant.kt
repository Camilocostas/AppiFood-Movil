package com.example.appifood_movil.data.model

import com.example.appifood_movil.R

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
    val dishes: List<Dish> = emptyList()
)

val sampleRestaurants = listOf(
    Restaurant(
        name = "China Express",
        address = "Calle 10 #5-20",
        imageRes = R.drawable.restaurantechino,
        schedule = "11:00 AM - 9:00 PM",
        hasDelivery = true,
        rating = "4.8",
        dishes = listOf(
            Dish("Arroz Chaufa Especial", "$ 25.000", R.drawable.arrozchaufa),
            Dish("Lomo Saltado Chino", "$ 28.500", R.drawable.lomosaltado),
            Dish("Tallarín Saltarín", "$ 22.000", R.drawable.tallarinsaltarin)
        )
    ),
    Restaurant(
        name = "La verdura",
        address = "Calle 17 #7-28",
        imageRes = R.drawable.restaurantevegano,
        schedule = "9:00 AM - 10:00 PM",
        hasDelivery = false,
        rating = "4.6",
        dishes = listOf(
            Dish("Ensalada César Premium", "$ 18.000", R.drawable.ensaladacesar),
            Dish("Bowl Vegano Mixto", "$ 22.000", R.drawable.bowlvegano),
            Dish("Hamburguesa de Lenteja", "$ 15.500", R.drawable.hamburguesalenteja)
        )
    )
)

fun searchRestaurants(query: String): List<Restaurant> {
    return sampleRestaurants.filter { restaurant ->
        restaurant.name.contains(query, ignoreCase = true) ||
                restaurant.address.contains(query, ignoreCase = true)
    }
}
