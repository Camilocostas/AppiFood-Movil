package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController) {
    val appiFoodRed = Color(0xFFFF4B3A)
    val lightGray = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Suscripción",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appiFoodRed)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Información del usuario
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(appiFoodRed)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8A80)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Mauricio Bustamante",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "yo@ejemplo.com",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }

            // Plan actual
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0EE)),
                    border = BorderStroke(1.dp, appiFoodRed.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Plan actual",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Gratis",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = appiFoodRed
                        )
                        Text(
                            text = "Disfruta de beneficios premium",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Otros planes disponibles
            item {
                Text(
                    text = "Otros planes disponibles",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Plan Gratis
            item {
                PlanCard(
                    title = "Gratis",
                    price = "\$0",
                    isCurrentPlan = true,
                    onSelect = { /* Ya es el plan actual */ }
                )
            }

            // Plan Premium Ahorro
            item {
                PlanCard(
                    title = "Premium Ahorro",
                    price = "\$7.900/mes",
                    isCurrentPlan = false,
                    onSelect = { /* Cambiar a este plan */ }
                )
            }

            // Espacio entre secciones
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Historial de facturación
            item {
                Text(
                    text = "Historial de facturación",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Tabla de facturación
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Column {
                        // Encabezados
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(lightGray)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fecha",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Concepto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1.5f)
                            )
                            Text(
                                text = "Monto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.End
                            )
                        }

                        // Fila vacía
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No hay facturas registradas",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }

            // Espacio final
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    isCurrentPlan: Boolean,
    onSelect: () -> Unit
) {
    val appiFoodRed = Color(0xFFFF4B3A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (isCurrentPlan) {
            BorderStroke(2.dp, appiFoodRed)
        } else {
            BorderStroke(1.dp, Color(0xFFEEEEEE))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de plan
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrentPlan) appiFoodRed.copy(alpha = 0.1f)
                        else Color(0xFFEEEEEE)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (title.contains("Premium")) {
                        Icons.Default.Star
                    } else {
                        Icons.Default.Person
                    },
                    contentDescription = null,
                    tint = if (isCurrentPlan) appiFoodRed else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información del plan
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (isCurrentPlan) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = appiFoodRed.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Plan actual",
                                fontSize = 10.sp,
                                color = appiFoodRed,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = price,
                    fontSize = 14.sp,
                    color = if (isCurrentPlan) appiFoodRed else Color.Gray
                )
            }

            // Botón de acción
            if (!isCurrentPlan) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appiFoodRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .width(100.dp)
                ) {
                    Text(
                        text = "Cambiar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}