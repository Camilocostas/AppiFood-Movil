// data/model/Review.kt
package com.example.appifood_movil.data.model

data class Review(
    val id: String = "",
    val restaurantUid: String = "",       // UID del restaurante
    val restaurantName: String = "",      // Nombre del restaurante (para consultas rápidas)
    val userId: String = "",              // UID del usuario que escribe
    val userName: String = "",            // Nombre completo del usuario
    val userPhoto: String = "",           // URL de la foto de perfil (opcional)
    val rating: Int = 0,                  // 1-5 estrellas
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)