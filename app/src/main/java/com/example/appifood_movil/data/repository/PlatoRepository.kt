package com.example.appifood_movil.data.repository

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import com.example.appifood_movil.data.model.Adicion
import android.app.Application

data class Plato(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val precioPromocion: Double = 0.0,
    val descuento: Int = 0,
    val categoria: String = "General",
    val imagenUrl: String = "",
    val disponible: Boolean = true,
    val adiciones: List<Adicion> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)


@Singleton
class PlatoRepository @Inject constructor() {

    fun getPlatos(restauranteId: String): Flow<List<Plato>> = flow {
        emit(emptyList())
    }

    suspend fun savePlato(
        restauranteId: String,
        plato: Plato,
        imagenUri: Uri?
    ) {}

    suspend fun toggleDisponible(restauranteId: String, platoId: String) {}

    suspend fun setPromocion(
        restauranteId: String,
        platoId: String,
        precioPromocion: Double,
        descuento: Int
    ) {}

    suspend fun deletePlato(restauranteId: String, platoId: String) {}
}
