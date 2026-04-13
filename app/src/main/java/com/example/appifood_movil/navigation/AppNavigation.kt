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
        // 1. Cambiamos el destino inicial a "onboarding"
        startDestination = "onboarding"
    ) {

        // 2. Definimos la nueva pantalla de Onboarding
        composable("onboarding") {
            OnboardingScreen(onFinished = {
                // Cuando el usuario termine las páginas, lo mandamos al login (auth)
                navController.navigate("auth") {
                    // Limpiamos el historial para que no pueda volver atrás al Onboarding
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        // 3. Mantenemos el AuthScreen (Login/Registro)
        composable("auth") {
            AuthScreen(onLoginNavigation = {
                navController.navigate("home") {
                    popUpTo("auth") { inclusive = true }
                }
            })
        }

        // Home
        composable("home") {
            HomeScreen(navController)
        }

        // Restaurant Detail
        composable("restaurantDetail/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            RestaurantDetailScreen(navController, name)
        }

        // Cart
        composable("cart") { CartScreen(navController) }

        // Product Detail
        composable(
            route = "productDetail/{name}/{price}/{image}",
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

        // Profile & Others
        composable("profile") { ProfileScreen(navController) }

        composable("orderHistory") {
            OrderHistoryScreen(navController)
        }

        composable("favorites") {
            FavoritesScreen(navController)
        }
    }
}