package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Favorito(
    val nombre: String,
    val precio: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {

    val favoritos = listOf(
        Favorito("Cheeseburger", "$25.000"),
        Favorito("Pizza", "$40.000"),
        Favorito("Sushi", "$30.000")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(favoritos) { item ->

                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.Red
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(item.nombre, fontWeight = FontWeight.Bold)
                                Text(item.precio, color = Color(0xFFFF4B3A))
                            }
                        }

                        Button(
                            onClick = { /* luego puedes navegar al detalle */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF4B3A)
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCart, null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}