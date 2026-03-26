package com.example.appifood_movil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.appifood_movil.ui.screens.AuthScreen
import com.example.appifood_movil.ui.screens.HomeScreen
import com.example.appifood_movil.ui.screens.ProductDetailScreen
import com.example.appifood_movil.ui.screens.SplashLoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashLoginScreen(
                onLoginClick = { navController.navigate(Screen.Auth.route) },
                onSignUpClick = { navController.navigate(Screen.Auth.route) }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(onLoginNavigation = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.ProductDetail.route) {
            ProductDetailScreen(navController)
        }
    }
}
