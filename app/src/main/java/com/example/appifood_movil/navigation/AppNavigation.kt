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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun AppNavigation(searchViewModel: SearchViewModel) {
    val navController = rememberNavController()

    // Estado de autenticación
    var isAuthenticated by remember { mutableStateOf(false) }
    var isRestaurant by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Verificar autenticación al iniciar
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        isAuthenticated = currentUser != null

        if (currentUser != null) {
            // Verificar si es restaurante
            try {
                val firestore = FirebaseFirestore.getInstance()
                val doc = firestore.collection("restaurants").document(currentUser.uid).get().await()
                isRestaurant = doc.exists()
            } catch (e: Exception) {
                isRestaurant = false
            }
        }
        isLoading = false
    }

    // Determinar destino inicial
    val startDestination = if (isLoading) {
        Screen.Splash.route
    } else if (isAuthenticated) {
        if (isRestaurant) Screen.RestaurantDashboard.route else Screen.Home.route
    } else {
        Screen.Splash.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    // Verificar nuevamente al terminar el splash
                    val auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser

                    if (user != null) {
                        // Usuario autenticado - verificar si es restaurante
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("restaurants").document(user.uid).get()
                            .addOnSuccessListener { doc ->
                                if (doc.exists()) {
                                    navController.navigate(Screen.RestaurantDashboard.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                // Si falla, ir a Home como cliente
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                    } else {
                        // No autenticado → onboarding
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
                    // ✅ Elimina Splash y Onboarding — RoleSelection es la nueva raíz
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        // Busca esta línea y reemplázala:
        composable(Screen.Auth.route) {
            AuthScreen(onLoginNavigation = {
                // ✅ popUpTo(0) limpia TODO el backstack — Splash, Onboarding,
                // RoleSelection y Auth quedan eliminados. Home es la nueva raíz.
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

        composable(Screen.OrderConfirmation.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderConfirmationScreen(
                navController = navController,
                orderId       = orderId
            )
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
            route     = Screen.ProductDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ProductDetailScreen(navController = navController, id = id)
        }

        composable(
            route     = Screen.RestaurantDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            RestaurantDetailScreen(navController = navController, id = id)
        }

        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
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