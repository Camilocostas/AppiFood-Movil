// data/api/response/OtherResponses.kt
package com.example.appifood_movil.data.api.response

data class UserResponse(
    val id    : Int,
    val name  : String,
    val email : String,
    val role  : String
)

data class MessageResponse(val message: String)
data class HealthResponse(val status: String)

data class ReviewsResponse(val data: List<ReviewDto>)
data class CartResponse(val data: Any? = null)
data class FavoritesResponse(val data: List<RestaurantDto> = emptyList())
data class NotificationsResponse(val data: List<Any> = emptyList())