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
fun ProductDetailScreen(
    navController: NavController,
    name: String,           // nombre -> name
    price: String,          // precio -> price
    imageRes: Int           // imagenRes -> imageRes (se mantiene igual)
) {
    var quantity by remember { mutableStateOf(1) } // cantidad -> quantity
    var extraCheese by remember { mutableStateOf(false) } // extraQueso -> extraCheese

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                painter = painterResource(id = imageRes),
                contentDescription = name,
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
                    Text(text = name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(text = price, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF4B3A))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Description", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(
                    text = "Our delicious $name is prepared with the highest quality fresh ingredients. A burst of flavor in every bite.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Ingredients", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(
                    text = "• Premium meat\n• Melted cheese\n• Fresh vegetables\n• Special house sauce",
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Add-ons", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

                // Movimos el FilterChip dentro de la columna para que sea visible
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = extraCheese,
                        onClick = { extraCheese = !extraCheese },
                        label = { Text("Extra Cheese +$2.000") },
                        leadingIcon = if (extraCheese) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )

                    // Usamos la función corregida CustomChip
                    CustomChip(label = "Bacon +$3.000")
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { /* TODO: Add to cart logic */ },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A)),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Add to Cart", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomChip(label: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(vertical = 5.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp
        )
    }
}