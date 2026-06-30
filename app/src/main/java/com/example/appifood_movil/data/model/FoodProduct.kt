package com.example.appifood_movil.data.model

import android.R

data class FoodProduct(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",          // ← URL de la imagen subida (prioridad)
    val imageRes: Int = R.drawable.alert_dark_frame, // ← Fallback local
    val category: String = "",
    val restauranteId: Int = 0,
    val description: String = "",
    val disponible: Boolean = true,
    val destacado: Boolean = false
)