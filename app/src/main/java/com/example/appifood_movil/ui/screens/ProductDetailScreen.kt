package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.items
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appifood_movil.ui.components.AppiFoodFooter
import com.example.appifood_movil.ui.viewmodel.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    id: Int,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadProduct(id)
    }

    var adicionesSeleccionadas by remember { mutableStateOf(setOf<String>()) }
    var cantidad by remember { mutableStateOf(1) }
    
    val appNaranja = Color(0xFFFF4B3A)

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = appNaranja)
        }
        return
    }

    val productData = product ?: return

    Scaffold(
        bottomBar = {
            Column {
                // 1. Panel de acción (Contador y Botón)
                Surface(shadowElevation = 12.dp, color = Color.White) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Contador de cantidad
                        Row(
                            modifier = Modifier
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5F5F5)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (cantidad > 1) cantidad-- }) { Icon(Icons.Default.Remove, null) }
                            Text("$cantidad", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
                            IconButton(onClick = { cantidad++ }) { Icon(Icons.Default.Add, null) }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Botón de agregar
                        Button(
                            onClick = { /* Acción */ },
                            colors = ButtonDefaults.buttonColors(appNaranja),
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Agregar al carrito", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 2. Footer de navegación (Menú)
                AppiFoodFooter(
                    navController = navController,
                    currentRoute = "home",
                    cartCount = 6,
                    onSearchClick = { navController.navigate("search") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header: Imagen y Título integrados
            item {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = productData.imageRes),
                        contentDescription = productData.name
                    )

                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.6f)))))

                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Text(productData.name, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = "$ ${String.format("%,.0f", productData.price).replace(",", ".")}",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FloatingActionButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(top = 54.dp, start = 16.dp)
                            .size(44.dp),
                        shape = CircleShape,
                        containerColor = Color.White.copy(alpha = 0.5f),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            }

            // Sección de Adiciones (Solo el texto)
            item {
                Text("Personaliza tu pedido", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp))
            }

            // Lista de opciones
            val opciones = listOf("Papas Fritas", "Extra Queso", "Tocino", "Salsa Especial")
            items(opciones) { item ->
                val isSelected = adicionesSeleccionadas.contains(item)
                val backgroundColor by animateColorAsState(if (isSelected) appNaranja.copy(0.1f) else Color.White)
                val borderColor by animateColorAsState(if (isSelected) appNaranja else Color.LightGray.copy(0.3f))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .clickable { adicionesSeleccionadas = if (isSelected) adicionesSeleccionadas - item else adicionesSeleccionadas + item },
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, borderColor)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(item, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = appNaranja))
                    }
                }
            }
        }
    }
}
