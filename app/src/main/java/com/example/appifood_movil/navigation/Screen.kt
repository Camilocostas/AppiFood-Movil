package com.example.appifood_movil.navigation

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

    object RestaurantDetail : Screen("restaurantDetail/{id}") {
        fun passId(id: Int): String = "restaurantDetail/$id"
    }

    object ProductDetail : Screen("productDetail/{id}") {
        fun passId(id: Int): String = "productDetail/$id"
    }

    // Añade esta línea a tu Screen.kt existente, dentro del sealed class
    object OrderConfirmation : Screen("orderConfirmation/{orderId}") {
        fun passId(orderId: String): String = "orderConfirmation/$orderId"
    }
}