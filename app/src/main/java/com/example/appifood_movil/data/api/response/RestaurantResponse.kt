package com.example.appifood_movil.data.api.response

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String? = null
)

data class RestaurantDto(
    val id: Int,
    val name: String,
    val description: String?,
    val address: String,
    val phone: String?,
    val email: String?,
    val logo: String?,
    val banner: String?,
    val image: String?,
    val rating: Double,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("delivery_cost") val deliveryCost: Double,
    @SerializedName("delivery_time_min") val deliveryTimeMin: Int,
    @SerializedName("delivery_time_max") val deliveryTimeMax: Int,
    val time: String?,
    @SerializedName("is_open") val isOpen: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_verified") val isVerified: Boolean,
    val lat: Double?,
    val lng: Double?,
    val latitude: Double?,
    val longitude: Double?,
    val schedule: List<ScheduleDto>? = emptyList()
)

data class ScheduleDto(
    val id: Int,
    val day: String,
    @SerializedName("opening_time") val openingTime: String,
    @SerializedName("closing_time") val closingTime: String,
    @SerializedName("is_closed") val isClosed: Boolean
)
