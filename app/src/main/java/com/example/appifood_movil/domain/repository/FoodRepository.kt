// domain/repository/FoodRepository.kt
package com.example.appifood_movil.domain.repository

import com.example.appifood_movil.domain.model.FoodProduct
import com.example.appifood_movil.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getRestaurants(): Flow<List<Restaurant>>

    // ✅ Add suspend here
    suspend fun getProducts(): List<FoodProduct>

    suspend fun getProductById(id: Int): FoodProduct?

    // ⚠️ Check this one too! If searchRestaurants filters locally,
    // it's fine as a normal fun. But in your Impl it calls
    // getRestaurantsFromFirestore() which is suspend, so make this suspend too!
    suspend fun searchRestaurants(query: String): List<Restaurant>

    suspend fun getRestaurantById(id: Int): Restaurant?
    suspend fun getProductFromFirestore(productId: Int): FoodProduct?
}