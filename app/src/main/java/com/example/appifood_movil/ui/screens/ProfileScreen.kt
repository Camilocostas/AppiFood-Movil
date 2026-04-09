package com.example.appifood_movil.ui.screens

import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.R

@Composable
fun ProfileScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF4B3A), Color(0xFFFF4B3A))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 50.dp, start = 20.dp, end = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }

                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) { Icon(Icons.Default.Edit, null, tint = Color.White) }
                        }

                        Box(contentAlignment = Alignment.BottomEnd) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_placeholder),
                                contentDescription = "Foto Mauricio",
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color.White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                color = Color.White,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(36.dp)
                                    .offset(x = (-3).dp, y = (-3).dp),
                                shadowElevation = 5.dp
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    null,
                                    tint = Color(0xFFFF4B3A),
                                    modifier = Modifier.padding(8.dp).size(20.dp)
                                )
                            }
                        }

                        Text("Mauricio Bustamante", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(top = 10.dp))
                        Text("mauricio@ejemplo.com", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(30.dp))

                ProfileSection(
                    titulo = "Detalles Personales",
                    icono = Icons.Default.Person,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    InfoRowItem("Dirección", "Calle 123 #45-67, Pasto", Icons.Default.LocationOn)
                    InfoRowItem("Teléfono", "+57 300 123 4567", Icons.Default.Phone)
                }

                Spacer(modifier = Modifier.height(20.dp))

                ProfileSection(
                    titulo = "Métodos de Pago",
                    icono = Icons.Default.CreditCard,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    InfoRowItem("Principal", "Mastercard **** 1234", Icons.Default.Payment, true)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp).height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
@Composable
fun ProfileSection(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, null, tint = Color(0xFFFF4B3A), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp), color = Color(0xFFF0F0F0))
            Column(content = content)
        }
    }
}

@Composable
fun InfoRowItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPayment: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFFFF4B3A).copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) { Icon(icon, null, tint = Color(0xFFFF4B3A), modifier = Modifier.padding(10.dp)) }

        Spacer(modifier = Modifier.width(15.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }

        if (!isPayment) {
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}