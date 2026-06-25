// data/api/response/OrdersResponse.kt
package com.example.appifood_movil.data.api.response

import com.google.gson.annotations.SerializedName

data class OrdersResponse(
    val data : List<OrderDto>
)

data class OrderCreatedResponse(
    val data    : OrderDto,
    val message : String = ""
)

data class OrderDto(
    val id               : Int,
    @SerializedName("restaurant_name")
    val restaurantName   : String = "",
    @SerializedName("delivery_address")
    val deliveryAddress  : String = "",
    @SerializedName("payment_method")
    val paymentMethod    : String = "",
    val status           : String = "pending",
    val total            : Double = 0.0,
    @SerializedName("created_at")
    val createdAt        : String = "",
    val items            : List<OrderItemDto> = emptyList()
)

data class OrderItemDto(
    val name     : String = "",
    val quantity : Int    = 0,
    val price    : Double = 0.0
)