package com.example.appifood_movil.data.api.response

import com.google.gson.annotations.SerializedName

data class RestaurantCreatedResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: RestaurantData? = null
)

data class RestaurantData(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("address")
    val address: String = "",

    @SerializedName("phone")
    val phone: String = "",

    @SerializedName("uid")
    val uid: String = ""
)