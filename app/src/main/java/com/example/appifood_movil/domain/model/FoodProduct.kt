// domain/model/FoodProduct.kt
package com.example.appifood_movil.domain.model

import com.example.appifood_movil.data.model.Adicion

data class FoodProduct(
    val id          : Int,
    val name        : String,
    val price       : Double,
    val imageRes    : Int,
    val categoria       : String    = "",
    val description : String    = "",
    // ── Campos de Firestore ───────────────────────────────────────
    val imagenUrl        : String       = "",   // URL de imagen en Storage
    val precioPromocion  : Double       = 0.0,
    val descuento        : Int          = 0,
    val disponible       : Boolean      = true,
    val adiciones        : List<Adicion> = emptyList(),
    // ── Para saber de qué restaurante viene ───────────────────────
    val restaurantId     : Int          = 0,
    val restaurantName   : String       = ""
)