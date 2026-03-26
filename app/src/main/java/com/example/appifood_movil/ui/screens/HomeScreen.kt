package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.appifood_movil.ui.theme.AppifoodMovilTheme


// Importa el R de tu proyecto para acceder a las imágenes
import com.example.appifood_movil.R

@Composable
fun HomeScreen(navController: NavController) {

    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    AnimatedVisibility(
        visible = startAnimation,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(600)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // --- CABECERA CURVA CON IMAGEN ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(410.dp)
                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.burger_background_2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "Que deseas Comer\nHoy?",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Buscar...") },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(55.dp),
                        shape = RoundedCornerShape(25.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CategoryText("Hamburguesas", active = true)
                        CategoryText("Sushi")
                        CategoryText("Bebidas")
                        CategoryText("Sopas")
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(5) {
                    FoodItemCard(navController)
                }
            }

            Spacer(modifier = Modifier.height(142.dp))

            BottomNavigationBar()
        }
    }
}

@Composable
fun FoodItemCard(navController: NavController) {
    Box(
        modifier = Modifier.width(220.dp).height(280.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Tarjeta blanca de fondo
        Card(
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth().height(220.dp).align(Alignment.BottomCenter)
                .clickable {
                    navController.navigate("productDetail")
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Cheeseburger ")
                        withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                            append("KFC")
                        }
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$25,000", color = Color(0xFFFF4B3A), fontWeight = FontWeight.Bold, fontSize = 19.sp)
            }
        }
        // Imagen circular que sobresale
        Image(
            painter = painterResource(id = R.drawable.burger_product), // Imagen de la hamburguesa
            contentDescription = null,
            modifier = Modifier
                .size(215.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun CustomBottomNavigation() {
    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Barra Roja
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color(0xFFFF5232)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(40.dp)) // Espacio para el botón flotante
            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
        }
        // Botón "+" flotante central
        FloatingActionButton(
            onClick = { },
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Color.Red,
            modifier = Modifier.offset(y = (-35).dp).size(60.dp).border(2.dp, Color.Red, CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun CategoryText(text: String, active: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
        if (active) {
            Box(modifier = Modifier.width(80.dp).height(4.dp).background(Color(0xFFFF4B3A)))
        }
    }
}

@Composable
fun BottomNavigationBar() {

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {

        // Barra roja
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Color(0xFFFF4B3A),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { }) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(40.dp))

            IconButton(onClick = { }) {
                Icon(Icons.Default.Message, contentDescription = null, tint = Color.White)
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
            }
        }

        // Botón + central
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y = (-42).dp)
                .border(4.dp, Color.White, CircleShape) // borde blanco
                .background(Color(0xFFFF4B3A), CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    AppifoodMovilTheme {
        HomeScreen(navController)
    }
}
