package com.example.appifood_movil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.appifood_movil.ui.screens.*
import androidx.compose.animation.*
import com.example.appifood_movil.ui.viewmodel.SearchViewModel

@Composable
fun AppNavigation(searchViewModel: SearchViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
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
            HomeScreen(
                navController = navController,
                searchViewModel = searchViewModel
            )
        }

        composable(
            route = Screen.Search.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            SearchScreen(viewModel = searchViewModel, navController = navController)
        }

        composable(
            route = "${Screen.RestaurantDetail.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            RestaurantDetailScreen(navController, id)
        }

        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }

        composable(
            route = "${Screen.ProductDetail.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ProductDetailScreen(navController, id)
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

        composable(Screen.Addresses.route) { 
            AddressesScreen(navController) 
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = { slideInVertically(initialOffsetY = { 500 }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { 500 }) + fadeOut() }
        ) { 
            SettingsScreen(navController) 
        }

        composable(Screen.Help.route) { 
            HelpCenterScreen(navController) 
        }
    }
}
