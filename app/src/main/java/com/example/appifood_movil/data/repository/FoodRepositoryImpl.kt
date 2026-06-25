// data/repository/FoodRepositoryImpl.kt
package com.example.appifood_movil.data.repository

import android.util.Log
import com.example.appifood_movil.data.allProducts
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.api.response.RestaurantDto
import com.example.appifood_movil.data.restaurants
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
        // 1. Emitir datos locales inmediatamente (UX sin espera)
        emit(restaurants)

        // 2. Intentar obtener datos frescos de la API
        try {
            val response = apiService.getRestaurants()
            if (response.isSuccessful) {
                val body = response.body()
                val domainList = body?.data?.map { it.toDomain() } ?: emptyList()
                if (domainList.isNotEmpty()) {
                    // Emitir datos de la API — reemplaza los locales
                    emit(domainList)
                    Log.d("FoodRepo", "Restaurantes de API: ${domainList.size}")
                }
            } else {
                Log.w("FoodRepo", "API error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            // Sin conexión o Railway en cold start — nos quedamos con locales
            Log.e("FoodRepo", "Error de red, usando datos locales: ${e.message}")
        }
    }

    override fun getProducts(): List<FoodProduct> = allProducts

    override suspend fun getProductById(id: Int): FoodProduct? =
        allProducts.find { it.id == id }

    override fun searchRestaurants(query: String): List<Restaurant> =
        restaurants.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }

    override suspend fun getRestaurantById(id: Int): Restaurant? {
        // Primero buscar en local (respuesta inmediata)
        val local = restaurants.find { it.id == id }

        return try {
            val response = apiService.getRestaurantById(id)
            if (response.isSuccessful) {
                response.body()?.data?.toDomain() ?: local
            } else {
                Log.w("FoodRepo", "getRestaurantById error ${response.code()}")
                local
            }
        } catch (e: Exception) {
            Log.e("FoodRepo", "Error obteniendo restaurante $id: ${e.message}")
            local
        }
    }

    // ── Mapper: RestaurantDto → Restaurant (dominio) ──────────────
    private fun RestaurantDto.toDomain(): Restaurant = Restaurant(
        id          = this.id,
        name        = this.name,
        phone       = this.phone,
        address     = this.address,
        imageUrl    = this.image ?: this.logo ?: "",
        imageRes    = com.example.appifood_movil.R.drawable.restaurantechino, // fallback local
        schedule    = this.time ?: "Horario no disponible",
        hasDelivery = this.deliveryCost > 0.0,
        rating      = this.averageRating.takeIf { it > 0 }?.toString()
            ?: this.rating,
        category    = this.category.ifBlank { "General" },
        latitude    = this.latitude ?: this.lat ?: 0.0,
        longitude   = this.longitude ?: this.lng ?: 0.0,
        dishes      = this.dishes.map { d ->
            Dish(name = d.name, price = d.price, imageRes =
                com.example.appifood_movil.R.drawable.cheese)
        },
        reviews     = this.reviews.map { r ->
            Review(user = r.user, comment = r.comment, rating = r.rating)
        }
    )
}