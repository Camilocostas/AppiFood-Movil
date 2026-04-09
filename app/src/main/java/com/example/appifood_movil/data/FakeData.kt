package com.example.appifood_movil.data

import com.example.appifood_movil.R

data class Plato(
    val nombre: String,
    val precio: String,
    val imagenRes: Int
)

data class Restaurante(
    val nombre: String,
    val direccion: String,
    val imagenRes: Int,
    val horario: String,
    val tieneDomicilio: Boolean,
    val calificacion: String = "4.5",
    val platos: List<Plato>
)

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: String,
    val imagen: Int,
    val categoria: String
)

val restaurantes = listOf(
    Restaurante(
        nombre = "China Express",
        direccion = "Calle 10 #5-20",
        imagenRes = R.drawable.restaurantechino,
        horario = "11:00 AM - 9:00 PM",
        tieneDomicilio = true,
        calificacion = "4.8",
        platos = listOf(
            Plato("Arroz Chaufa Especial", "$ 25.000", R.drawable.arrozchaufa),
            Plato("Lomo Saltado Chino", "$ 28.500", R.drawable.lomosaltado),
            Plato("Tallarín Saltarín", "$ 22.000", R.drawable.tallarinsaltarin)
        )
    ),
    Restaurante(
        nombre = "La verdura",
        direccion = "Calle 17 #7-28",
        imagenRes = R.drawable.restaurantevegano,
        horario = "9:00 AM - 10:00 PM",
        tieneDomicilio = false,
        calificacion = "4.6",
        platos = listOf(
            Plato("Ensalada César Premium", "$ 18.000", R.drawable.ensaladacesar),
            Plato("Bowl Vegano Mixto", "$ 22.000", R.drawable.bowlvegano),
            Plato("Hamburguesa de Lenteja", "$ 15.500", R.drawable.hamburguesalenteja)
        )
    )
)

fun buscarRestaurantes(query: String): List<Restaurante> {
    return restaurantes.filter { restaurante ->
        restaurante.nombre.contains(query, ignoreCase = true) ||
                restaurante.direccion.contains(query, ignoreCase = true)
    }
}