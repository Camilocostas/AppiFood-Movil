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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.clickable

@Composable
fun AppiFoodFooter(
    navController: NavController,
    currentRoute: String,
    cartCount: Int = 0,
    onSearchClick: () -> Unit
) {
    val activeColor = Color(0xFFFF4B3A)
    val inactiveColor = Color.Gray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(73.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = if (currentRoute == "home") activeColor else inactiveColor,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        if (currentRoute != "home") navController.navigate("home")
                    }
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = inactiveColor,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onSearchClick() }
            )

            BadgedBox(
                badge = {
                    if (cartCount > 0) {
                        Badge(containerColor = activeColor) {
                            Text("$cartCount", fontSize = 9.sp)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = if (currentRoute == "cart") activeColor else inactiveColor,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable {
                            if (currentRoute != "cart") navController.navigate("cart")
                        }
                )
            }

            Icon(
                imageVector = Icons.Outlined.Assignment,
                contentDescription = null,
                tint = if (currentRoute == "orderHistory") activeColor else inactiveColor,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        if (currentRoute != "orderHistory") navController.navigate("orderHistory")
                    }
            )

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (currentRoute == "profile") activeColor else inactiveColor,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        if (currentRoute != "profile") navController.navigate("profile")
                    }
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .navigationBarsPadding()
        )
    }
}