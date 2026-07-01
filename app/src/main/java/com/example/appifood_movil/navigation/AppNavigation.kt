// navigation/AppNavigation.kt
package com.example.appifood_movil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.appifood_movil.ui.screens.*
import androidx.compose.animation.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.data.local.TokenManager

@Composable
fun AppNavigation(searchViewModel: SearchViewModel) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // ✅ Declaramos el ViewModel compartido para que persista entre pantallas
    val sharedCartViewModel: com.example.appifood_movil.ui.viewmodel.CartViewModel = androidx.hilt.navigation.compose.hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    if (tokenManager.isLoggedIn()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }


        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.RoleSelection.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Auth.route) {
            AuthScreen(onLoginNavigation = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }

        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(navController = navController)
        }

        composable(Screen.RestaurantAuth.route) {
            RestaurantAuthScreen(navController = navController)
        }

        composable(Screen.RestaurantDashboard.route) {
            RestaurantDashboardScreen(navController = navController)
        }

        composable(Screen.RestaurantOrders.route) {
            RestaurantOrdersScreen(navController = navController)
        }
        composable(Screen.RestaurantOrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            RestaurantOrderDetailScreen(
                navController = navController,
                orderId = orderId
            )
        }

        composable("gestionPlatos") { GestionPlatosScreen(navController = navController) }
        composable("gestionInfoRestaurante") { GestionInfoRestauranteScreen(navController = navController) }
        composable("gestionResenas") { GestionResenasScreen(navController = navController) }

        composable(Screen.OrderConfirmation.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderConfirmationScreen(navController = navController, orderId = orderId)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController, searchViewModel = searchViewModel)
        }

        composable(
            route = Screen.Search.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            SearchScreen(viewModel = searchViewModel, navController = navController)
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            // ✅ Pasamos el sharedCartViewModel para que se guarden los datos
            ProductDetailScreen(
                navController = navController,
                id = id,
                cartViewModel = sharedCartViewModel
            )
        }

        composable(
            route = Screen.RestaurantDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            RestaurantDetailScreen(navController = navController, id = id)
        }

        composable(Screen.Cart.route) {
            // ✅ Pasamos el sharedCartViewModel para ver los productos agregados
            CartScreen(
                navController = navController,
                cartViewModel = sharedCartViewModel
            )
        }

        composable(
            route = Screen.WriteReview.route,
            arguments = listOf(
                navArgument("restaurantUid") { type = NavType.StringType },
                navArgument("restaurantName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("restaurantUid") ?: ""
            val name = backStackEntry.arguments?.getString("restaurantName") ?: ""
            WriteReviewScreen(
                navController = navController,
                restaurantUid = uid,
                restaurantName = name
            )
        }

        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.OrderHistory.route) { OrderHistoryScreen(navController) }
        composable(Screen.Favorites.route) { FavoritesScreen(navController) }
        composable(Screen.Help.route) { HelpCenterScreen(navController) }
        composable(Screen.Subscription.route) { SubscriptionScreen(navController) }
        composable(Screen.Payments.route) { PaymentsScreen(navController) }
        composable(Screen.NotificationsCenter.route) { NotificationsCenterScreen(navController) }

        composable(
            route = Screen.Settings.route,
            enterTransition = { slideInVertically(initialOffsetY = { 500 }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { 500 }) + fadeOut() }
        ) {
            SettingsScreen(navController)
        }
    }
}
