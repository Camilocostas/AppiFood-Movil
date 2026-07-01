// ui/screens/ProductDetailScreen.kt
package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.Adicion
import com.example.appifood_movil.ui.components.AppiFoodFooter
import com.example.appifood_movil.ui.viewmodel.CartViewModel
import com.example.appifood_movil.ui.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val RedPrimary   = Color(0xFFD32F2F)
private val YellowAccent = Color(0xFFFFD600)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController : NavController,
    id            : Int,
    viewModel     : ProductDetailViewModel = hiltViewModel(),
    cartViewModel : CartViewModel          = hiltViewModel() // Recibe el compartido desde AppNavigation
) {
    val product     by viewModel.product.collectAsState()
    val isLoading   by viewModel.isLoading.collectAsState()
    val description by viewModel.description.collectAsState()
    val adiciones   by viewModel.adiciones.collectAsState()

    LaunchedEffect(id) { viewModel.loadProduct(id) }

    var adicionesSeleccionadas by remember { mutableStateOf(setOf<Adicion>()) }
    var cantidad               by remember { mutableIntStateOf(1) }

    val precioBase = if ((product?.precioPromocion ?: 0.0) > 0.0)
        product?.precioPromocion ?: 0.0
    else
        product?.price ?: 0.0

    val totalAdiciones = adicionesSeleccionadas.sumOf { it.precio }
    val totalUnitario  = precioBase + totalAdiciones
    val totalFinal     = totalUnitario * cantidad

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

    var isAddingToCart     by remember { mutableStateOf(false) }
    var showAddedAnimation by remember { mutableStateOf(false) }
    var buttonScale        by remember { mutableFloatStateOf(1f) }
    val scope              = rememberCoroutineScope()

    val animatedButtonScale by animateFloatAsState(
        targetValue   = buttonScale,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label         = "buttonScale"
    )

    if (isLoading || product == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = RedPrimary)
        }
        return
    }

    val productData = product!!

    Scaffold(
        bottomBar = {
            Column {
                Surface(shadowElevation = 12.dp, color = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                        if (adicionesSeleccionadas.isNotEmpty() || cantidad > 1) {
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total con adiciones:", fontSize = 13.sp, color = TextMuted)
                                Text(formatter.format(totalFinal), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = RedPrimary)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            AnimatedCounter(cantidad, { cantidad++ }, { if (cantidad > 1) cantidad-- })
                            Spacer(modifier = Modifier.width(16.dp))
                            AnimatedAddToCartButton(
                                isAdding = isAddingToCart,
                                showAdded = showAddedAnimation,
                                scale = animatedButtonScale,
                                onClick = {
                                    scope.launch {
                                        isAddingToCart = true
                                        buttonScale = 0.85f
                                        delay(150)
                                        buttonScale = 1.1f
                                        delay(150)
                                        buttonScale = 1f

                                        cartViewModel.addItem(
                                            id = productData.id,
                                            name = productData.name,
                                            price = totalUnitario.toInt(),
                                            quantity = cantidad,
                                            imageRes = productData.imageRes,
                                            imageUrl = productData.imagenUrl,
                                            adiciones = adicionesSeleccionadas.map { it.nombre }
                                        )

                                        showAddedAnimation = true
                                        delay(800)
                                        isAddingToCart = false
                                        showAddedAnimation = false
                                    }
                                }
                            )
                        }
                    }
                }
                AppiFoodFooter(navController, "home", 0, { navController.navigate("search") })
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(padding)) {
            item {
                ProductImageHeader(
                    imagenUrl = productData.imagenUrl,
                    imageRes = productData.imageRes,
                    name = productData.name,
                    price = productData.price,
                    precioPromocion = productData.precioPromocion,
                    onBack = { navController.popBackStack() }
                )
            }
            item { ProductDescriptionSection(description) }
            if (adiciones.isNotEmpty()) {
                item { AnimatedSectionHeader("Personaliza tu pedido", Icons.Default.Edit) }
                items(adiciones) { adicion ->
                    AdicionOptionCard(
                        adicion = adicion,
                        isSelected = adicionesSeleccionadas.contains(adicion),
                        formatter = formatter,
                        onToggle = {
                            adicionesSeleccionadas = if (adicionesSeleccionadas.contains(adicion))
                                adicionesSeleccionadas - adicion else adicionesSeleccionadas + adicion
                        }
                    )
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun ProductImageHeader(imagenUrl: String, imageRes: Int, name: String, price: Double, precioPromocion: Double, onBack: () -> Unit) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }
    Box(modifier = Modifier.fillMaxWidth().height(320.dp).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))) {
        if (imagenUrl.isNotEmpty()) {
            AsyncImage(model = imagenUrl, contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Image(painter = painterResource(id = if(imageRes != 0) imageRes else R.drawable.burguer), contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)), startY = 0.35f)))
        Surface(modifier = Modifier.align(Alignment.TopStart).padding(top = 54.dp, start = 16.dp).size(48.dp).clip(CircleShape).clickable { onBack() }, color = Color.White.copy(alpha = 0.3f), shadowElevation = 8.dp) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
        }
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
            Text(name, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text(formatter.format(if(precioPromocion > 0) precioPromocion else price), color = YellowAccent, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdicionOptionCard(adicion: Adicion, isSelected: Boolean, formatter: NumberFormat, onToggle: () -> Unit) {
    val scale by animateFloatAsState(if (isSelected) 1.02f else 1f, label = "adicionScale")
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp).graphicsLayer { scaleX = scale; scaleY = scale }.clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) RedPrimary.copy(alpha = 0.08f) else Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) RedPrimary else Color(0xFFE0E0E0))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(if (isSelected) RedPrimary else Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                Icon(if (isSelected) Icons.Default.Check else Icons.Default.Add, null, tint = if (isSelected) Color.White else TextMuted, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = adicion.nombre, modifier = Modifier.weight(1f), fontSize = 15.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
            Text(text = "+ ${formatter.format(adicion.precio)}", fontSize = 14.sp, color = if (isSelected) RedPrimary else TextMuted)
        }
    }
}

@Composable
fun AnimatedCounter(cantidad: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Surface(modifier = Modifier.height(56.dp).width(120.dp).clip(RoundedCornerShape(16.dp)), color = Color(0xFFF0F0F0)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = onDecrement) { Icon(Icons.Default.Remove, null, tint = RedPrimary) }
            Text(text = "$cantidad", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
            IconButton(onClick = onIncrement) { Icon(Icons.Default.Add, null, tint = RedPrimary) }
        }
    }
}

@Composable
fun AnimatedAddToCartButton(isAdding: Boolean, showAdded: Boolean, scale: Float, onClick: () -> Unit) {
    var buttonColor by remember { mutableStateOf(RedPrimary) }
    var buttonText by remember { mutableStateOf("Agregar al carrito") }

    LaunchedEffect(showAdded) {
        if (showAdded) {
            buttonColor = Color(0xFF4CAF50)
            buttonText = "¡Agregado!"
            delay(1500)
            buttonColor = RedPrimary
            buttonText = "Agregar al carrito"
        }
    }

    Button(
        onClick = onClick,
        enabled = !isAdding && !showAdded,
        modifier = Modifier.fillMaxWidth().height(56.dp).graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.White)
    ) {
        if (isAdding) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        else Text(buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProductDescriptionSection(description: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(description, color = TextMuted, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}

@Composable
fun AnimatedSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = RedPrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}