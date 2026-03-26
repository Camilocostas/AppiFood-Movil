package com.example.appifood_movil.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object ProductDetail : Screen("product_detail")
}
