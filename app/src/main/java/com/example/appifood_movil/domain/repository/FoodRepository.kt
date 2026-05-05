package com.example.appifood_movil.domain.repository

import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getRestaurants(): Flow<List<Restaurant>>
    fun getProducts(): List<FoodProduct>
    fun searchRestaurants(query: String): List<Restaurant>
}
