package com.example.appifood_movil.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AppiFoodFooter(navController: NavController, currentRoute: String, cartCount: Int = 0) {
    val activeColor = Color(0xFFFF4B3A)
    val inactiveColor = Color.Gray

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(70.dp)
    ) {
        // Inicio
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { if(currentRoute != "home") navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(26.dp)) },
            label = { Text("Inicio", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = activeColor,
                selectedTextColor = activeColor,
                unselectedIconColor = inactiveColor,
                indicatorColor = Color.Transparent
            )
        )

        // Buscar
        NavigationBarItem(
            selected = false,
            onClick = { /* Navegar a búsqueda */ },
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Buscar", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactiveColor)
        )

        // Carrito con Badge
        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = { navController.navigate("cart") },
            icon = {
                BadgedBox(badge = {
                    if (cartCount > 0) {
                        Badge(containerColor = activeColor) { Text("$cartCount", color = Color.White) }
                    }
                }) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null)
                }
            },
            label = { Text("Carrito", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactiveColor)
        )

        // Pedidos
        NavigationBarItem(
            selected = currentRoute == "orderHistory",
            onClick = {navController.navigate("orderHistory")},
            icon = { Icon(Icons.Outlined.Assignment, contentDescription = null) },
            label = { Text("Pedidos", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactiveColor)
        )

        // Perfil
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactiveColor)
        )
    }
}