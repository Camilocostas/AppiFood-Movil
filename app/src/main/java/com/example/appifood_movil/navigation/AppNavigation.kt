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

        composable("restaurantDetail/{nombre}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")
            RestaurantDetailScreen(navController, nombre ?: "")
        }

        composable("cart") { CartScreen(navController) }

        composable(
            route = Screen.ProductDetail.route + "/{nombre}/{precio}/{imagen}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("precio") { type = NavType.StringType },
                navArgument("imagen") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val precio = backStackEntry.arguments?.getString("precio") ?: ""
            val imagen = backStackEntry.arguments?.getInt("imagen") ?: 0
            ProductDetailScreen(navController, nombre, precio, imagen)
        }

        composable("profile") { ProfileScreen(navController) }

        composable("orderHistory") {
            OrderHistoryScreen(navController)
        }

        composable("favorites") {
            FavoritesScreen(navController)
        }
    }
}
