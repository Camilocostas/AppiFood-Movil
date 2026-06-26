package com.example.appifood_movil.domain.repository

import com.example.appifood_movil.domain.model.FoodProduct
import com.example.appifood_movil.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getRestaurants(): Flow<List<Restaurant>>
    suspend fun getRestaurantById(id: Int): Restaurant?
    fun getProducts(): List<FoodProduct>
    suspend fun getProductById(id: Int): FoodProduct?
    fun searchRestaurants(query: String): List<Restaurant>
}
