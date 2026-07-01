// data/api/response/OtherResponses.kt
package com.example.appifood_movil.data.api.response

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class MessageResponse(
    val success: Boolean = false,  // ← Agregar success
    val message: String = ""       // ← Con valor por defecto
)

data class HealthResponse(val status: String)

data class CartResponse(val data: Any? = null)
data class FavoritesResponse(val data: List<RestaurantDto> = emptyList())
data class NotificationsResponse(val data: List<Any> = emptyList())