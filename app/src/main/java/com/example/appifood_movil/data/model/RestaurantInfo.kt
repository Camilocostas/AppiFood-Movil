package com.example.appifood_movil.data.model

data class RestaurantInfo(
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val direccion: String = "",
    val telefono: String = "",  // ← Si se llama "telefono"
    val horario: String = "",
    val imagenPortada: String = "",
    val fotosGaleria: List<String> = emptyList()
)