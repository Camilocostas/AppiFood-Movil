package com.example.appifood_movil.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object RestaurantDetail : Screen("restaurantDetail")
    object ProductDetail : Screen("productDetail")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object OrderHistory : Screen("orderHistory")
    object Favorites : Screen("favorites")
}