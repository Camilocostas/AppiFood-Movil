package com.example.appifood_movil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.appifood_movil.ui.screens.*
import androidx.compose.animation.*
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import com.example.appifood_movil.ui.screens.FavoritesScreen

@Composable
fun AppNavigation(searchViewModel: SearchViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route // Usamos la constante
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, searchViewModel = searchViewModel)
        }

        composable(
            route = "search",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            // AQUÍ ESTÁ EL TRUCO: Pasamos EL MISMO searchViewModel
            SearchScreen(viewModel = searchViewModel, navController = navController)
        }

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
            // AQUÍ ESTÁ EL CAMBIO: Le pasamos el searchViewModel
            HomeScreen(
                navController = navController,
                searchViewModel = searchViewModel
            )
        }

        composable(
            route = "${Screen.RestaurantDetail.route}/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            RestaurantDetailScreen(navController, name)
        }

        composable("cart") {
            CartScreen(navController = navController)
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
        composable("favorites") { FavoritesScreen(navController) }
        composable("addresses") { AddressesScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("help") { HelpCenterScreen(navController) }
        composable(
            route = "settings",
            enterTransition = { slideInVertically(initialOffsetY = { 500 }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { 500 }) + fadeOut() }
        ) { SettingsScreen(navController) }
    }
}