package com.example.appifood_movil.data.repository

import com.example.appifood_movil.data.allProducts
import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.data.restaurants
import com.example.appifood_movil.data.searchRestaurants
import com.example.appifood_movil.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor() : FoodRepository {

    override fun getRestaurants(): Flow<List<Restaurant>> = flow {
        // Simulamos una carga de datos
        emit(restaurants)
    }

    override fun getProducts(): List<FoodProduct> {
        return allProducts
    }

    override fun searchRestaurants(query: String): List<Restaurant> {
        return searchRestaurants(query)
    }
}
