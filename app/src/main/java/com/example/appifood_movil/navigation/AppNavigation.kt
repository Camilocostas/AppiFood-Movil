package com.example.appifood_movil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.appifood_movil.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route // Usamos la constante
    ) {

        composable(Screen.Splash.route) {
            // Asumiendo que tenías una pantalla de splash o onboarding
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
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

        composable(
            route = "${Screen.RestaurantDetail.route}/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            RestaurantDetailScreen(navController, name)
        }

        composable(Screen.Cart.route) {
            CartScreen(navController)
        }

        composable(
            route = "${Screen.ProductDetail.route}/{name}/{price}/{image}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType },
                navArgument("image") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val price = backStackEntry.arguments?.getString("price") ?: ""
            val image = backStackEntry.arguments?.getInt("image") ?: 0
            ProductDetailScreen(navController, name, price, image)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(navController)
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(navController)
        }
    }
}