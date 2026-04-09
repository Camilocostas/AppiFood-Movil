package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, nombre: String, precio: String, imagenRes: Int) {
    var cantidad by remember { mutableStateOf(1) }
    var extraQueso by remember { mutableStateOf(false) }

    FilterChip(
        selected = extraQueso,
        onClick = { extraQueso = !extraQueso },
        label = { Text("Extra Queso +$2.000") },
        leadingIcon = if (extraQueso) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = imagenRes),
                contentDescription = nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = nombre, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(text = precio, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF4B3A))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Descripción", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(
                    text = "Nuestra deliciosa $nombre preparada con ingredientes frescos de la mejor calidad. Una explosión de sabor en cada bocado.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Ingredientes", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(
                    text = "• Carne premium\n• Queso fundido\n• Vegetales frescos\n• Salsa especial de la casa",
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Adiciones", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AsistChip(label = "Tocineta +$3.000")
                    AsistChip(label = "Huevo +$2.000")
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A)),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Añadir al carrito", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AsistChip(label: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(vertical = 5.dp)
    ) {
        Text(text = label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp)
    }
}