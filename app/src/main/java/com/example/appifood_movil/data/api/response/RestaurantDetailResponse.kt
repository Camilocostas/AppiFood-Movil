// data/api/response/RestaurantDetailResponse.kt
package com.example.appifood_movil.data.api.response

import com.google.gson.annotations.SerializedName

data class RestaurantDetailResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: RestaurantDto? = null
)

data class RestaurantDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("logo")
    val logo: String? = null,

    @SerializedName("time")
    val time: String? = null,

    @SerializedName("deliveryCost")
    val deliveryCost: Double? = null,

    @SerializedName("averageRating")
    val averageRating: Double? = null,

    @SerializedName("rating")
    val rating: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("lng")
    val lng: Double? = null,

    @SerializedName("uid")
    val uid: String? = null,

    @SerializedName("estado")
    val estado: String? = null,

    @SerializedName("dishes")
    val dishes: List<DishDto>? = emptyList(),

    @SerializedName("reviews")
    val reviews: List<ReviewDto>? = emptyList()
)

data class DishDto(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("price")
    val price: Double? = null
)

data class ReviewDto(
    @SerializedName("user")
    val user: String? = null,

    @SerializedName("comment")
    val comment: String? = null,

    @SerializedName("rating")
    val rating: Int? = null
)