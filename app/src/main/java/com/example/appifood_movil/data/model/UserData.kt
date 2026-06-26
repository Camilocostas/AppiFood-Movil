package com.example.appifood_movil.data.model

data class UserData(
    val names: String = "",
    val lastNames: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val imageUrl: String = "",  // ✅ Nuevo campo para la foto de perfil
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", "", System.currentTimeMillis())
}