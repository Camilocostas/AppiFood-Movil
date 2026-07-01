package com.example.appifood_movil.data.api.response

import com.google.gson.annotations.SerializedName

data class RestaurantsResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: List<RestaurantDto> = emptyList()
)