package com.example.appifood_movil.navigation
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")  // ⭐ AGREGADO
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Search : Screen("search")
    object RestaurantDetail : Screen("restaurant_detail")
    object Cart : Screen("cart")
    object ProductDetail : Screen("product_detail")
    object Profile : Screen("profile")
    object OrderHistory : Screen("order_history")
    object Favorites : Screen("favorites")
    object Addresses : Screen("addresses")
    object Settings : Screen("settings")
    object Help : Screen("help")

    // Nuevas rutas para el perfil
    object Subscription : Screen("subscription")
    object Payments : Screen("payments")
    object NotificationsCenter : Screen("notifications_center")
    object Orders : Screen("orders")
}