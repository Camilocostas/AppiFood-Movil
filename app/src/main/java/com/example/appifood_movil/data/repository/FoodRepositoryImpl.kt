// data/repository/FoodRepositoryImpl.kt
package com.example.appifood_movil.data.repository

import android.util.Log
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.api.response.RestaurantDto
import com.example.appifood_movil.domain.model.Dish
import com.example.appifood_movil.domain.model.FoodProduct
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
            if (response.isSuccessful) {
                val body = response.body()
                val domainList = body?.data?.map { it.toDomain() } ?: emptyList()
                emit(domainList)
            }
        } catch (e: Exception) {
            Log.e("FoodRepo", "❌ Error: ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun getProducts(): List<FoodProduct> = emptyList()

    override suspend fun getProductById(id: Int): FoodProduct? = null

    override suspend fun searchRestaurants(query: String): List<Restaurant> = emptyList()

    override suspend fun getProductFromFirestore(productId: Int): FoodProduct? = null

    override suspend fun getRestaurantById(id: Int): Restaurant? {
        return try {
            val response = apiService.getRestaurantById(id)
            if (response.isSuccessful) {
                response.body()?.data?.toDomain()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun RestaurantDto.toDomain(): Restaurant = Restaurant(
        id = this.id,
        name = this.name,
        phone = this.phone ?: "",
        address = this.address ?: "",
        description = "",
        imageUrl = this.image ?: this.logo ?: "",
        imageRes = com.example.appifood_movil.R.drawable.restaurantechino,
        schedule = this.time ?: "Horario no disponible",
        hasDelivery = (this.deliveryCost ?: 0.0) > 0.0,
        rating = this.averageRating?.takeIf { it > 0 }?.toString()
            ?: this.rating ?: "4.5",
        category = this.category?.ifBlank { "General" } ?: "General",
        latitude = this.latitude ?: this.lat ?: 0.0,
        longitude = this.longitude ?: this.lng ?: 0.0,
        uid = this.uid ?: "",
        estado = this.estado ?: "activo",
        dishes = this.dishes?.map { d ->
            Dish(
                name = d.name ?: "",
                price = d.price ?: 0.0,
                imageRes = com.example.appifood_movil.R.drawable.burguer
            )
        } ?: emptyList(),
        reviews = this.reviews?.map { r ->
            Review(
                user = r.user ?: "",
                comment = r.comment ?: "",
                rating = r.rating ?: 5
            )
        } ?: emptyList()
    )
}
