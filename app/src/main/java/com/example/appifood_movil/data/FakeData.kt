package com.example.appifood_movil.data

import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.Dish
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.data.model.FoodProduct



val restaurants = listOf(
    Restaurant(
        name = "China Express",
        address = "Calle 10 #5-20",
        imageRes = R.drawable.restaurantechino,
        schedule = "11:00 AM - 9:00 PM",
        hasDelivery = true,
        rating = "4.8",
        category = "Comida China",
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
        category = "Comida Vegana",
        dishes = listOf(
            Dish("Ensalada César Premium", "$ 18.000", R.drawable.ensaladacesar),
            Dish("Bowl Vegano Mixto", "$ 22.000", R.drawable.bowlvegano),
            Dish("Hamburguesa de Lenteja", "$ 15.500", R.drawable.hamburguesalenteja)
        )
    )
)

val allProducts = listOf(
    FoodProduct(1, "Cheeseburger", "$25.000", R.drawable.cheese, "Hamburguesas"),
    FoodProduct(2, "Big Mac", "$32.000", R.drawable.bicmac, "Hamburguesas"),
    FoodProduct(7, "Hamburguesa", "$15.000", R.drawable.clasica, "Hamburguesas"),
    FoodProduct(3, "Philadelphia", "$28.000", R.drawable.philadelphia, "Sushi"),
    FoodProduct(4, "Ojo de Tigre", "$35.000", R.drawable.ojotigre, "Sushi"),
    FoodProduct(5, "Coca Cola 1L", "$6.000", R.drawable.cocacola, "Bebidas"),
    FoodProduct(6, "Ramen Tonkotsu", "$22.000", R.drawable.ramen, "Sopas")
)

fun searchRestaurants(query: String): List<Restaurant> {
    return restaurants.filter { restaurant ->
        restaurant.name.contains(query, ignoreCase = true) ||
                restaurant.address.contains(query, ignoreCase = true)
    }
}