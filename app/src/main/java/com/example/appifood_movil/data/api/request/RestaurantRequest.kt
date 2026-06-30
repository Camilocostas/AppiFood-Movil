package com.example.appifood_movil.data.api.request

import com.google.gson.annotations.SerializedName

data class RestaurantRequest(
    @SerializedName("name")
    val name: String = "",

    @SerializedName("address")
    val address: String = "",

    @SerializedName("phone")
    val phone: String = "",

    @SerializedName("category")
    val category: String = "",

    @SerializedName("schedule")
    val schedule: String = "",

    @SerializedName("latitude")
    val latitude: Double = 0.0,

    @SerializedName("longitude")
    val longitude: Double = 0.0,

    @SerializedName("deliveryCost")
    val deliveryCost: Double = 0.0,

    @SerializedName("time")
    val time: String = "",

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("logo")
    val logo: String? = null,

    @SerializedName("uid")
    val uid: String = "",

    @SerializedName("estado")
    val estado: String = "activo"
)