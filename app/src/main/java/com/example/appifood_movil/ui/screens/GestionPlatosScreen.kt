package com.example.appifood_movil.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.Adicion
import com.example.appifood_movil.data.repository.Plato
import com.example.appifood_movil.ui.viewmodel.PlatoViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

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
        topBar = {
            TopAppBar(
                title = { Text("Mis Platos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar plato")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (platos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Text("No tienes platos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Agrega tu primer plato", color = Color.Gray)
                    Button(onClick = { showAddDialog = true }) {
                        Text("Agregar plato")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(platos) { plato ->
                    PlatoCardCompleto(
                        plato = plato,
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
                    // Para agregar nuevo plato
                    if (showAddDialog) {
                        AddPlatoDialog(
                            restauranteId = restauranteId,
                            onDismiss = { showAddDialog = false },
                            onSave = { nombre, descripcion, precio, categoria, adiciones, imagenUri ->  // ✅ Recibir categoría
                                viewModel.savePlato(
                                    restauranteId = restauranteId,
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precio,
                                    categoria = categoria,  // ✅ Usar la categoría seleccionada
                                    adiciones = adiciones,
                                    imagenUri = imagenUri
                                )
                                showAddDialog = false
                                Toast.makeText(context, "Plato agregado", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

// Para editar plato
                    if (showEditDialog != null) {
                        AddPlatoDialog(
                            plato = showEditDialog,
                            restauranteId = restauranteId,
                            onDismiss = { showEditDialog = null },
                            onSave = { nombre, descripcion, precio, categoria, adiciones, imagenUri ->  // ✅ Recibir categoría
                                viewModel.updatePlato(
                                    restauranteId = restauranteId,
                                    platoId = showEditDialog!!.id,
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precio,
                                    categoria = categoria,  // ✅ Usar la categoría seleccionada
                                    adiciones = adiciones,
                                    imagenUri = imagenUri
                                )
                                showEditDialog = null
                                Toast.makeText(context, "Plato actualizado", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }}}}}

    showPromocionDialog?.let { plato ->
        PromocionDialog(
            plato = plato,
            restauranteId = restauranteId,
            onDismiss = { showPromocionDialog = null },
            onSave = { precioPromocion, descuento ->
                viewModel.setPromocion(
                    restauranteId = restauranteId,
                    platoId = plato.id,
                    precioPromocion = precioPromocion,
                    descuento = descuento
                )
                showPromocionDialog = null
                Toast.makeText(context, "Promoción aplicada", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun PlatoCardCompleto(
    plato              : Plato,
    onEdit             : () -> Unit,  // ✅ AGREGAR
    onToggleDisponible : () -> Unit,
    onDelete           : () -> Unit,
    onPromocion        : () -> Unit
) {
    val formatter  = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }
    var isPressed  by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "platoScale"
    )

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable { isPressed = true },  // ✅ Click en la card
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Banda de estado arriba ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        if (plato.disponible) Color(0xFF1D9E75)
                        else Color(0xFFE53935)
                    )
            )

            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Imagen ────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    if (plato.imagenUrl.isNotEmpty()) {
                        AsyncImage(
                            model              = plato.imagenUrl,
                            contentDescription = plato.nombre,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter            = painterResource(id = R.drawable.burguer),
                            contentDescription = plato.nombre,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        )
                    }
                    // Badge disponible sobre imagen
                    Surface(
                        modifier  = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp),
                        shape     = RoundedCornerShape(6.dp),
                        color     = if (plato.disponible) Color(0xFF1D9E75) else Color(0xFFE53935)
                    ) {
                        Text(
                            text     = if (plato.disponible) "✓" else "✗",
                            color    = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // ── Info ──────────────────────────────────────────
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = plato.nombre,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 16.sp,
                        color      = Color(0xFF1A1A1A),
                        maxLines   = 1
                    )
                    Text(
                        text     = plato.descripcion,
                        fontSize = 12.sp,
                        color    = Color(0xFF888888),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Precios
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text       = formatter.format(plato.precio),
                            fontWeight = FontWeight.Bold,
                            fontSize   = if (plato.precioPromocion > 0) 12.sp else 16.sp,
                            color      = if (plato.precioPromocion > 0) Color(0xFFAAAAAA)
                            else Color(0xFF1565C0),
                            textDecoration = if (plato.precioPromocion > 0)
                                androidx.compose.ui.text.style.TextDecoration.LineThrough
                            else androidx.compose.ui.text.style.TextDecoration.None
                        )
                        if (plato.precioPromocion > 0) {
                            Text(
                                text       = formatter.format(plato.precioPromocion),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 16.sp,
                                color      = Color(0xFFE53935)
                            )
                            if (plato.descuento > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFEBEB)
                                ) {
                                    Text(
                                        text       = "-${plato.descuento}%",
                                        color      = Color(0xFFE53935),
                                        fontSize   = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier   = Modifier.padding(
                                            horizontal = 6.dp, vertical = 2.dp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (plato.adiciones.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1565C0).copy(alpha = 0.08f)
                        ) {
                            Text(
                                text     = "➕ ${plato.adiciones.size} adiciones",
                                fontSize = 10.sp,
                                color    = Color(0xFF1565C0),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // ── Acciones verticales ───────────────────────────
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ✅ Botón EDITAR
                    Box(
                        modifier         = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1565C0).copy(alpha = 0.1f))
                            .clickable { onEdit() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit, null,
                            tint     = Color(0xFF1565C0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    // Toggle disponible
                    Box(
                        modifier         = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (plato.disponible) Color(0xFF1D9E75).copy(alpha = 0.1f)
                                else Color(0xFFE53935).copy(alpha = 0.1f)
                            )
                            .clickable { onToggleDisponible() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (plato.disponible) Icons.Default.CheckCircle
                            else Icons.Default.Cancel,
                            null,
                            tint     = if (plato.disponible) Color(0xFF1D9E75)
                            else Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Promoción
                    Box(
                        modifier         = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF57F17).copy(alpha = 0.1f))
                            .clickable { onPromocion() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocalOffer, null,
                            tint     = Color(0xFFF57F17),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    // Eliminar
                    Box(
                        modifier         = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935).copy(alpha = 0.1f))
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Delete, null,
                            tint     = Color(0xFFE53935),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
// ── CATEGORÍAS PREDEFINIDAS ──────────────────────────────────────
private val CATEGORIAS_PREDEFINIDAS = listOf(
    "Todos",
    "Bebidas",
    "Postres",
    "Rapida",
    "Oriental",
    "Mexicana",
    "Vegetariana"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlatoDialog(
    plato: Plato? = null,
    restauranteId: String,
    onDismiss: () -> Unit,
    onSave: (nombre: String, descripcion: String, precio: Double, categoria: String, adiciones: List<Adicion>, imagenUri: Uri?) -> Unit  // ✅ Agregar categoria
) {
    val context = LocalContext.current
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

    // ✅ Estado para categoría
    var categoria by remember { mutableStateOf(plato?.categoria ?: "General") }
    // ✅ Si es edición, cargar los datos del plato
    var nombre by remember { mutableStateOf(plato?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(plato?.descripcion ?: "") }
    var precio by remember { mutableStateOf(plato?.precio?.toString() ?: "") }
    var adiciones by remember { mutableStateOf(plato?.adiciones ?: emptyList()) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var showAddAdicion by remember { mutableStateOf(false) }
    var editandoAdicionIndex by remember { mutableStateOf<Int?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imagenUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    if (plato == null) "Agregar Plato" else "Editar Plato",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── IMAGEN ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5))
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
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                                Text("Toca para seleccionar foto", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── CAMPOS ──────────────────────────────────────────
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del plato") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        CATEGORIAS_PREDEFINIDAS.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoria = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── ADICIONES ──────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Adiciones disponibles", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    TextButton(onClick = { showAddAdicion = true }) {
                        Text("Agregar adición")
                    }
                }

                if (adiciones.isNotEmpty()) {
                    adiciones.forEachIndexed { index, adicion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                                    color = Color.Gray
                                )
                            }
                            Row {
                                // ✅ Botón EDITAR
                                IconButton(
                                    onClick = { editandoAdicionIndex = index },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Color(0xFF1565C0),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                // Botón ELIMINAR
                                IconButton(
                                    onClick = { adiciones = adiciones.filterIndexed { i, _ -> i != index } },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text("No hay adiciones agregadas", color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── BOTONES ─────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val precioDouble = precio.toDoubleOrNull()
                            if (nombre.isNotBlank() && precioDouble != null && precioDouble > 0) {
                                onSave(nombre, descripcion, precioDouble, categoria, adiciones, imagenUri)  // ✅ Pasar categoría
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nombre y precio válido son requeridos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text(if (plato == null) "Guardar" else "Actualizar")
                    }
                }
            }
        }
    }

    // ✅ Dialog para AGREGAR adición
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

    // ✅ Dialog para EDITAR adición
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

@Composable
fun AddAdicionDialog(
    adicionExistente: Adicion? = null,  // ✅ NUEVO
    onDismiss: () -> Unit,
    onSave: (nombre: String, precio: Double) -> Unit
) {
    var nombre by remember { mutableStateOf(adicionExistente?.nombre ?: "") }
    var precio by remember { mutableStateOf(adicionExistente?.precio?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    if (adicionExistente == null) "Agregar Adición" else "Editar Adición",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la adición") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio adicional") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text(if (adicionExistente == null) "Agregar" else "Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun PromocionDialog(
    plato: Plato,
    restauranteId: String,
    onDismiss: () -> Unit,
    onSave: (precioPromocion: Double, descuento: Int) -> Unit
) {
    val context = LocalContext.current
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

    var precioPromocion by remember { mutableStateOf(plato.precioPromocion.toString()) }
    var descuento by remember { mutableStateOf(plato.descuento.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Promoción para ${plato.nombre}", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Precio original: ${formatter.format(plato.precio)}",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = precioPromocion,
                    onValueChange = { precioPromocion = it },
                    label = { Text("Precio en promoción") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descuento,
                    onValueChange = { descuento = it },
                    label = { Text("Porcentaje de descuento (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            onSave(0.0, 0)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFE53935)
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("Aplicar promoción")
                    }
                }
            }
        }
    }
}