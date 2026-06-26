// data/api/request/OrderRequest.kt
package com.example.appifood_movil.data.api.request

data class OrderRequest(
    val restaurant_id    : Int,
    val delivery_address : String,
    val payment_method   : String,
    val items            : List<OrderItemRequest>
)

data class OrderItemRequest(
    val product_id : Int,
    val quantity   : Int
)