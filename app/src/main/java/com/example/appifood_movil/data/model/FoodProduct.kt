package com.example.appifood_movil.data.model

import com.example.appifood_movil.R

data class FoodProduct(
    val id: Int,
    val name: String,
    val price: String,
    val imageRes: Int,
    val category: String,
    val description: String = "AppiFood Special"
)

// Datos estáticos de ejemplo (se moverán a un Repositorio después)
val sampleProducts = listOf(
    FoodProduct(1, "Cheeseburger", "$25.000", R.drawable.cheese, "Hamburguesas"),
    FoodProduct(2, "Big Mac", "$32.000", R.drawable.bicmac, "Hamburguesas"),
    FoodProduct(7, "Hamburguesa", "$15.000", R.drawable.clasica, "Hamburguesas"),
    FoodProduct(3, "Philadelphia", "$28.000", R.drawable.philadelphia, "Sushi"),
    FoodProduct(4, "Ojo de Tigre", "$35.000", R.drawable.ojotigre, "Sushi"),
    FoodProduct(5, "Coca Cola 1L", "$6.000", R.drawable.cocacola, "Bebidas"),
    FoodProduct(6, "Ramen Tonkotsu", "$22.000", R.drawable.ramen, "Sopas")
)
