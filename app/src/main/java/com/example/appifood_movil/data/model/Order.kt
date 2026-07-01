// data/model/Order.kt
package com.example.appifood_movil.data.model

data class Order(
    val orderId         : String         = "",
    val timestamp       : Long           = 0L,
    val status          : String         = "pending",
    val customer        : CustomerInfo   = CustomerInfo(),
    val restaurant      : RestaurantInfo = RestaurantInfo(),
    val items           : List<OrderItem> = emptyList(),
    val payment         : PaymentInfo    = PaymentInfo(),
    val restaurantUid: String = "",
    val deliveryAddress : String         = "",
    val subtotal        : Int            = 0,
    val shipping        : Int            = 0,
    val total           : Int            = 0
) {
    // Constructor sin args requerido por Firestore
    constructor() : this(
        orderId = "", timestamp = 0L, status = "pending",
        customer = CustomerInfo(), restaurant = RestaurantInfo(),
        items = emptyList(), payment = PaymentInfo(),
        deliveryAddress = "", subtotal = 0, shipping = 0, total = 0
    )
}

data class CustomerInfo(
    val uid      : String = "",
    val fullName : String = "",
    val phone    : String = ""
) { constructor() : this("", "", "") }



data class OrderItem(
    val name     : String = "",
    val quantity : Int    = 0,
    val price    : Int    = 0,
    val subtotal : Int    = 0
) { constructor() : this("", 0, 0, 0) }

data class PaymentInfo(
    val method : String = "",
    val detail : String = ""
) { constructor() : this("", "") }