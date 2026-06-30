package com.example.appifood_movil.navigation

import android.net.Uri
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Search : Screen("search")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object OrderHistory : Screen("orderHistory")
    object Favorites : Screen("favorites")
    object Addresses : Screen("addresses")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object Subscription : Screen("subscription")
    object Payments : Screen("payments")
    object NotificationsCenter : Screen("notificationsCenter")
    object RoleSelection     : Screen("roleSelection")
    object RestaurantAuth    : Screen("restaurantAuth")
    object RestaurantDashboard : Screen("restaurantDashboard")
    object WriteReview : Screen("writeReview/{restaurantUid}/{restaurantName}") {
        fun passData(restaurantUid: String, restaurantName: String) =
            "writeReview/$restaurantUid/${Uri.encode(restaurantName)}"
    }

    object RestaurantOrders : Screen("restaurant_orders")
    object RestaurantOrderDetail : Screen("restaurant_order_detail/{orderId}") {
        fun passOrderId(orderId: String) = "restaurant_order_detail/$orderId"
    }

    object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun passId(orderId: String) = "order_confirmation/$orderId"
    }
    object RestaurantDetail : Screen("restaurantDetail/{id}") {
        fun passId(id: Int): String = "restaurantDetail/$id"
    }

    object ProductDetail : Screen("productDetail/{id}") {
        fun passId(id: Int): String = "productDetail/$id"
    }
}