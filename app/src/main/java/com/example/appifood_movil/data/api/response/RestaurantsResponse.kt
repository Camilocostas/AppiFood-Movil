// data/api/response/RestaurantsResponse.kt
package com.example.appifood_movil.data.api.response

import com.google.gson.annotations.SerializedName

data class RestaurantsResponse(
    val data : List<RestaurantDto>
)

data class RestaurantDetailResponse(
    val data : RestaurantDto
)

data class RestaurantDto(
    val id       : Int,
    val name     : String,
    val address  : String    = "",
    val phone    : String    = "",
    val category : String    = "",
    val rating   : String    = "4.5",
    // Laravel puede devolver el campo imagen con distintos nombres
    // SerializedName permite mapear cualquiera de ellos
    @SerializedName("image")  val image  : String? = null,
    @SerializedName("logo")   val logo   : String? = null,
    @SerializedName("time")   val time   : String? = null,
    @SerializedName("average_rating") val averageRating : Double = 4.5,
    @SerializedName("delivery_cost") val deliveryCost  : Double = 0.0,
    val latitude  : Double?  = null,
    val longitude : Double?  = null,
    val lat       : Double?  = null,
    val lng       : Double?  = null,
    val dishes    : List<DishDto>   = emptyList(),
    val reviews   : List<ReviewDto> = emptyList()
)

data class DishDto(
    val id    : Int    = 0,
    val name  : String = "",
    val price : Double = 0.0,
    val image : String? = null
)

data class ReviewDto(
    val user    : String = "",
    val comment : String = "",
    val rating  : Int    = 5
)