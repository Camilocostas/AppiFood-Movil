package com.example.appifood_movil.data.repository

import com.example.appifood_movil.data.allProducts
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.api.response.RestaurantDto
import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.domain.model.Dish
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.model.Review
import com.example.appifood_movil.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : FoodRepository {

    override fun getRestaurants(): Flow<List<Restaurant>> = flow {
        try {
            val response = apiService.getRestaurants()
            if (response.success) {
                val domainRestaurants = response.data.map { it.toDomain() }
                emit(domainRestaurants)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getProducts(): List<FoodProduct> {
        return allProducts
    }

    override suspend fun getProductById(id: Int): FoodProduct? {
        return allProducts.find { it.id == id }
    }

    override fun searchRestaurants(query: String): List<Restaurant> {
        return emptyList()
    }

    override suspend fun getRestaurantById(id: Int): Restaurant? {
        return try {
            val response = apiService.getRestaurantById(id)
            if (response.success) {
                response.data.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun RestaurantDto.toDomain(): Restaurant {
        return Restaurant(
            id = this.id,
            name = this.name,
            address = this.address,
            imageUrl = this.image ?: this.logo,
            schedule = this.time ?: "Horario no disponible",
            hasDelivery = this.deliveryCost > 0.0,
            rating = this.averageRating.toString(),
            category = "General",
            latitude = this.latitude ?: this.lat ?: 0.0,
            longitude = this.longitude ?: this.lng ?: 0.0,
            dishes = emptyList(), 
            reviews = emptyList()
        )
    }
}
