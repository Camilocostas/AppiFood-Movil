package com.example.appifood_movil.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.Adicion
import com.example.appifood_movil.data.repository.Plato
import com.example.appifood_movil.ui.viewmodel.PlatoViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.text.NumberFormat
import androidx.compose.foundation.lazy.LazyRow
import java.util.Locale

// ─── COLORES Y FORMATO ──────────────────────────────────────────────
private val PrimaryColor = Color(0xFF1565C0)
private val PrimaryLight = Color(0xFF42A5F5)
private val SuccessColor = Color(0xFF1D9E75)
private val ErrorColor = Color(0xFFE53935)
private val WarningColor = Color(0xFFFF9800)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)
private val SurfaceColor = Color(0xFFFFFFFF)
private val BackgroundGray = Color(0xFFF5F7FA)

private val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

// ─── CATEGORÍAS ──────────────────────────────────────────────────────
private val CATEGORIAS = listOf("Todos", "Bebidas", "Postres", "Rapida", "Oriental", "Mexicana", "Vegetariana")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPlatosScreen(
    navController: NavController,
    viewModel: PlatoViewModel = hiltViewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val restauranteId = user?.uid ?: ""
    val platos by viewModel.platos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showPromocionDialog by remember { mutableStateOf<Plato?>(null) }
    var showEditDialog by remember { mutableStateOf<Plato?>(null) }

    LaunchedEffect(restauranteId) {
        if (restauranteId.isNotEmpty()) {
            viewModel.loadPlatos(restauranteId)
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🍽️ Mis Platos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = PrimaryColor,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar plato")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceColor,
                    scrolledContainerColor = SurfaceColor
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else if (platos.isEmpty()) {
                EmptyStateView(onAddClick = { showAddDialog = true })
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(platos) { index, plato ->
                        AnimatedPlatoCard(
                            plato = plato,
                            index = index,
                            onEdit = { showEditDialog = plato },
                            onToggleDisponible = {
                                viewModel.toggleDisponible(restauranteId, plato.id)
                            },
                            onDelete = {
                                viewModel.deletePlato(restauranteId, plato.id)
                                Toast.makeText(context, "Plato eliminado", Toast.LENGTH_SHORT).show()
                            },
                            onPromocion = { showPromocionDialog = plato }
                        )
                    }
                }
            }

            // ─── DIÁLOGOS ──────────────────────────────────────────────
            if (showAddDialog) {
                AddEditPlatoDialog(
                    restauranteId = restauranteId,
                    onDismiss = { showAddDialog = false },
                    onSave = { nombre, descripcion, precio, categoria, adiciones, imagenUri ->
                        viewModel.savePlato(
                            restauranteId = restauranteId,
                            nombre = nombre,
                            descripcion = descripcion,
                            precio = precio,
                            categoria = categoria,
                            adiciones = adiciones,
                            imagenUri = imagenUri
                        )
                        showAddDialog = false
                        Toast.makeText(context, "¡Plato agregado!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showEditDialog != null) {
                AddEditPlatoDialog(
                    plato = showEditDialog,
                    restauranteId = restauranteId,
                    onDismiss = { showEditDialog = null },
                    onSave = { nombre, descripcion, precio, categoria, adiciones, imagenUri ->
                        viewModel.updatePlato(
                            restauranteId = restauranteId,
                            platoId = showEditDialog!!.id,
                            nombre = nombre,
                            descripcion = descripcion,
                            precio = precio,
                            categoria = categoria,
                            adiciones = adiciones,
                            imagenUri = imagenUri
                        )
                        showEditDialog = null
                        Toast.makeText(context, "¡Plato actualizado!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showPromocionDialog != null) {
                PromocionDialog(
                    plato = showPromocionDialog!!,
                    restauranteId = restauranteId,
                    onDismiss = { showPromocionDialog = null },
                    onSave = { precioPromocion, descuento ->
                        viewModel.setPromocion(
                            restauranteId = restauranteId,
                            platoId = showPromocionDialog!!.id,
                            precioPromocion = precioPromocion,
                            descuento = descuento
                        )
                        showPromocionDialog = null
                        Toast.makeText(context, "Promoción aplicada", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

// ─── ANIMATED PLATO CARD ─────────────────────────────────────────────
@Composable
fun AnimatedPlatoCard(
    plato: Plato,
    index: Int,
    onEdit: () -> Unit,
    onToggleDisponible: () -> Unit,
    onDelete: () -> Unit,
    onPromocion: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = EaseOutQuad),
        label = "cardAlpha"
    )

    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 30.dp,
        animationSpec = spring(Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "cardOffset"
    )

    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                translationY = offsetY.value
            }
            .clickable { isPressed = true },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Banda de estado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        if (plato.disponible) SuccessColor else ErrorColor
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen con badge
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                ) {
                    if (plato.imagenUrl.isNotEmpty()) {
                        AsyncImage(
                            model = plato.imagenUrl,
                            contentDescription = plato.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.burguer),
                            contentDescription = plato.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Badge de estado
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(5.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (plato.disponible) SuccessColor else ErrorColor,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (plato.disponible) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (plato.disponible) "Disponible" else "No disponible",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info del plato
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plato.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )

                    Text(
                        text = plato.descripcion.take(40),
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Precios
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formatter.format(plato.precio),
                            fontWeight = FontWeight.Bold,
                            fontSize = if (plato.precioPromocion > 0) 13.sp else 17.sp,
                            color = if (plato.precioPromocion > 0) TextSecondary else PrimaryColor,
                            textDecoration = if (plato.precioPromocion > 0) TextDecoration.LineThrough else TextDecoration.None
                        )

                        if (plato.precioPromocion > 0) {
                            Text(
                                text = formatter.format(plato.precioPromocion),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = ErrorColor
                            )

                            if (plato.descuento > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ErrorColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "-${plato.descuento}% OFF",
                                        color = ErrorColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (plato.adiciones.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${plato.adiciones.size} adiciones",
                                fontSize = 11.sp,
                                color = PrimaryColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Acciones
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ActionButton(
                        icon = Icons.Default.Edit,
                        tint = PrimaryColor,
                        onClick = onEdit
                    )
                    ActionButton(
                        icon = if (plato.disponible) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        tint = if (plato.disponible) SuccessColor else ErrorColor,
                        onClick = onToggleDisponible
                    )
                    ActionButton(
                        icon = Icons.Default.LocalOffer,
                        tint = WarningColor,
                        onClick = onPromocion
                    )
                    ActionButton(
                        icon = Icons.Default.Delete,
                        tint = ErrorColor,
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.1f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

// ─── ESTADO VACÍO ──────────────────────────────────────────────────
@Composable
fun EmptyStateView(onAddClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(Spring.DampingRatioMediumBouncy))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = PrimaryColor.copy(alpha = 0.1f),
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = PrimaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No tienes platos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Agrega tu primer plato y comienza a vender",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar plato", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── DIÁLOGO AGREGAR/EDITAR PLATO ──────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlatoDialog(
    plato: Plato? = null,
    restauranteId: String,
    onDismiss: () -> Unit,
    onSave: (nombre: String, descripcion: String, precio: Double, categoria: String, adiciones: List<Adicion>, imagenUri: Uri?) -> Unit
) {
    val context = LocalContext.current

    var categoria by remember { mutableStateOf(plato?.categoria ?: "General") }
    var nombre by remember { mutableStateOf(plato?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(plato?.descripcion ?: "") }
    var precio by remember { mutableStateOf(plato?.precio?.toString() ?: "") }
    var adiciones by remember { mutableStateOf(plato?.adiciones ?: emptyList()) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    var showAddAdicion by remember { mutableStateOf(false) }
    var editandoAdicionIndex by remember { mutableStateOf<Int?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagenUri = uri
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 30.dp)
                .shadow(20.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (plato == null) "✨ Agregar Plato" else "✏️ Editar Plato",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F2F5))
                        .clickable { launcher.launch("image/*") }
                ) {
                    when {
                        imagenUri != null -> {
                            AsyncImage(
                                model = imagenUri,
                                contentDescription = "Foto del plato",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        plato?.imagenUrl?.isNotEmpty() == true -> {
                            AsyncImage(
                                model = plato.imagenUrl,
                                contentDescription = "Foto del plato",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                                Text(
                                    text = "Toca para seleccionar foto",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campos
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del plato") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Categoría con chips
                Text(
                    text = "Categoría",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                var selectedCategoria by remember { mutableStateOf(categoria) }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CATEGORIAS) { cat ->
                        FilterChip(
                            selected = selectedCategoria == cat,
                            onClick = { selectedCategoria = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            modifier = Modifier,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryColor.copy(alpha = 0.15f),
                                selectedLabelColor = PrimaryColor
                            )
                        )
                    }
                }
                // Sincronizar con el estado de la categoría
                LaunchedEffect(selectedCategoria) {
                    categoria = selectedCategoria
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Adiciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧂 Adiciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    TextButton(onClick = { showAddAdicion = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                if (adiciones.isNotEmpty()) {
                    adiciones.forEachIndexed { index, adicion ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8F9FA)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = adicion.nombre,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = formatter.format(adicion.precio),
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = { editandoAdicionIndex = index },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = PrimaryColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { adiciones = adiciones.filterIndexed { i, _ -> i != index } },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Eliminar",
                                            tint = ErrorColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No hay adiciones agregadas",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val precioDouble = precio.toDoubleOrNull()
                            if (nombre.isNotBlank() && precioDouble != null && precioDouble > 0) {
                                onSave(nombre, descripcion, precioDouble, categoria, adiciones, imagenUri)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nombre y precio válido son requeridos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (plato == null) "Guardar" else "Actualizar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Diálogo para agregar/editar adición
    if (showAddAdicion) {
        AddAdicionDialog(
            adicionExistente = null,
            onDismiss = { showAddAdicion = false },
            onSave = { nombreAdicion, precioAdicion ->
                adiciones = adiciones + Adicion(nombreAdicion, precioAdicion)
                showAddAdicion = false
            }
        )
    }

    editandoAdicionIndex?.let { index ->
        val adicion = adiciones[index]
        AddAdicionDialog(
            adicionExistente = adicion,
            onDismiss = { editandoAdicionIndex = null },
            onSave = { nombreAdicion, precioAdicion ->
                adiciones = adiciones.mapIndexed { i, a ->
                    if (i == index) a.copy(nombre = nombreAdicion, precio = precioAdicion) else a
                }
                editandoAdicionIndex = null
            }
        )
    }
}

// ─── DIÁLOGO AGREGAR/EDITAR ADICIÓN ───────────────────────────────
@Composable
fun AddAdicionDialog(
    adicionExistente: Adicion? = null,
    onDismiss: () -> Unit,
    onSave: (nombre: String, precio: Double) -> Unit
) {
    var nombre by remember { mutableStateOf(adicionExistente?.nombre ?: "") }
    var precio by remember { mutableStateOf(adicionExistente?.precio?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .shadow(16.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (adicionExistente == null) "➕ Agregar Adición" else "✏️ Editar Adición",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio adicional") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val precioDouble = precio.toDoubleOrNull()
                            if (nombre.isNotBlank() && precioDouble != null) {
                                onSave(nombre, precioDouble)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (adicionExistente == null) "Agregar" else "Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── DIÁLOGO DE PROMOCIÓN ──────────────────────────────────────────
@Composable
fun PromocionDialog(
    plato: Plato,
    restauranteId: String,
    onDismiss: () -> Unit,
    onSave: (precioPromocion: Double, descuento: Int) -> Unit
) {
    val context = LocalContext.current

    var precioPromocion by remember { mutableStateOf(plato.precioPromocion.toString()) }
    var descuento by remember { mutableStateOf(plato.descuento.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .shadow(16.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "🎯 Promoción para ${plato.nombre}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Precio original: ${formatter.format(plato.precio)}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = precioPromocion,
                    onValueChange = { precioPromocion = it },
                    label = { Text("Precio en promoción") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarningColor,
                        focusedLabelColor = WarningColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descuento,
                    onValueChange = { descuento = it },
                    label = { Text("Descuento (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarningColor,
                        focusedLabelColor = WarningColor
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSave(0.0, 0)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ErrorColor
                        )
                    ) {
                        Text("Quitar promoción")
                    }

                    Button(
                        onClick = {
                            val precio = precioPromocion.toDoubleOrNull()
                            val desc = descuento.toIntOrNull()
                            if (precio != null && precio > 0 && desc != null) {
                                onSave(precio, desc)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Precio y descuento válidos son requeridos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Aplicar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}