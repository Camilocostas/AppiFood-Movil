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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class PaymentMethod(
    val id: String,
    val type: String,
    val label: String,
    val holderName: String,
    val lastDigits: String,
    val isDefault: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(navController: NavController) {
    val appiFoodRed = Color(0xFFFF4B3A)
    var showAddPaymentForm by remember { mutableStateOf(false) }
    var paymentMethods by remember {
        mutableStateOf(
            listOf<PaymentMethod>()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Título vacío o solo el ícono
                    if (showAddPaymentForm) {
                        Text(
                            "Nuevo Método de Pago",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showAddPaymentForm) {
                            showAddPaymentForm = false
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            if (showAddPaymentForm) Icons.Default.Close else Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appiFoodRed)
            )
        }
    ) { paddingValues ->
        if (showAddPaymentForm) {
            AddPaymentForm(
                onSave = { newMethod ->
                    paymentMethods = paymentMethods + newMethod
                    showAddPaymentForm = false
                },
                onCancel = { showAddPaymentForm = false }
            )
        } else {
            // Lista de métodos de pago
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                // Header del perfil
                item {
                    ProfileHeader()
                }

                // Mensaje de error
                item {
                    ErrorCard()
                }

                // Botón Agregar Método de Pago
                item {
                    Button(
                        onClick = { showAddPaymentForm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appiFoodRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Agregar Método de Pago",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mis Métodos de Pago
                item {
                    Text(
                        text = "Mis Métodos de Pago",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                if (paymentMethods.isEmpty()) {
                    // Mensaje cuando no hay métodos de pago
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = Color.Gray.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No tienes métodos de pago registrados",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Agrega uno para comprar más rápido",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    // Lista de métodos de pago guardados
                    items(paymentMethods) { method ->
                        PaymentMethodCard(
                            method = method,
                            onSetDefault = { methodId ->
                                paymentMethods = paymentMethods.map {
                                    it.copy(isDefault = it.id == methodId)
                                }
                            },
                            onDelete = { methodId ->
                                paymentMethods = paymentMethods.filter { it.id != methodId }
                            }
                        )
                    }
                }

                // Cerrar sesión
                item {
                    LogoutButton(navController)
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    val appiFoodRed = Color(0xFFFF4B3A)

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

@Composable
fun ErrorCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0EE)),
        border = BorderStroke(1.dp, Color(0xFFFF4B3A).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFFF4B3A),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Error al cargar los métodos de pago",
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    method: PaymentMethod,
    onSetDefault: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val appiFoodRed = Color(0xFFFF4B3A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (method.isDefault) 2.dp else 1.dp,
            if (method.isDefault) appiFoodRed else Color(0xFFEEEEEE)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(appiFoodRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = appiFoodRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = method.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (method.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = appiFoodRed.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Predeterminado",
                                    fontSize = 10.sp,
                                    color = appiFoodRed,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${method.type} --- ${method.lastDigits}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Titular: ${method.holderName}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Menú de opciones
                var expanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (!method.isDefault) {
                            DropdownMenuItem(
                                text = { Text("Establecer como predeterminado") },
                                onClick = {
                                    onSetDefault(method.id)
                                    expanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red) },
                            onClick = {
                                onDelete(method.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddPaymentForm(
    onSave: (PaymentMethod) -> Unit,
    onCancel: () -> Unit
) {
    val appiFoodRed = Color(0xFFFF4B3A)
    var selectedType by remember { mutableStateOf("Tarjeta") }
    var label by remember { mutableStateOf("Método principal") }
    var holderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    val paymentTypes = listOf(
        "Tarjeta" to "Crédito o débito en una sola opción",
        "Nequi" to "Billetera digital",
        "Bancolombia" to "Transferencia / cuenta bancaria",
        "PSE" to "Pago por banco en línea"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Título del formulario
        item {
            Text(
                text = "Nuevo Método de Pago",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        // Tipo de método
        item {
            Text(
                text = "Tipo de método",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Opciones de tipo de pago
        items(paymentTypes) { (type, description) ->
            PaymentTypeOption(
                type = type,
                description = description,
                isSelected = selectedType == type,
                onClick = { selectedType = type }
            )
        }

        // Etiqueta
        item {
            Text(
                text = "Etiqueta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Método principal") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Nombre del titular
        item {
            Text(
                text = "Nombre del Titular *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }

        item {
            OutlinedTextField(
                value = holderName,
                onValueChange = { holderName = it },
                placeholder = { Text("Juan Pérez") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Número de tarjeta / cuenta
        if (selectedType == "Tarjeta") {
            item {
                Text(
                    text = "Número de Tarjeta *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    placeholder = { Text("1234 5678 9101 1121") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Fila de fecha y CVV
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it },
                        placeholder = { Text("Mes") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        placeholder = { Text("Año") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        placeholder = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        } else {
            // Para Nequi, Bancolombia, PSE
            item {
                Text(
                    text = "Número de teléfono / cuenta *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    placeholder = { Text("3001234567") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        }

        // Checkbox predeterminado
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { isDefault = !isDefault },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = appiFoodRed
                    )
                )
                Text(
                    text = "Usar como método de pago predeterminado",
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
        }

        // Botón Guardar
        item {
            Button(
                onClick = {
                    if (holderName.isNotBlank() && cardNumber.isNotBlank()) {
                        val lastDigits = cardNumber.takeLast(4)
                        val newMethod = PaymentMethod(
                            id = System.currentTimeMillis().toString(),
                            type = selectedType,
                            label = label,
                            holderName = holderName,
                            lastDigits = if (lastDigits.isNotEmpty()) lastDigits else "1234",
                            isDefault = isDefault
                        )
                        onSave(newMethod)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appiFoodRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = holderName.isNotBlank() && cardNumber.isNotBlank()
            ) {
                Text(
                    text = "Guardar Método de Pago",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PaymentTypeOption(
    type: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val appiFoodRed = Color(0xFFFF4B3A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) appiFoodRed else Color(0xFFEEEEEE)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF0EE) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = appiFoodRed
                )
            )
            Column {
                Text(
                    text = type,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 15.sp,
                    color = if (isSelected) appiFoodRed else Color(0xFF333333)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun LogoutButton(navController: NavController) {
    val appiFoodRed = Color(0xFFFF4B3A)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("auth") {
                    popUpTo("home") { inclusive = true }
                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ExitToApp,
            contentDescription = null,
            tint = appiFoodRed,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Cerrar sesión",
            color = appiFoodRed,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}