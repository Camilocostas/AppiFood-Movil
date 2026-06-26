// data/model/ReceiptItem.kt
package com.example.appifood_movil.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

// ── Por qué NO data class ─────────────────────────────────────────
// data class genera equals/hashCode/copy basados en los parámetros
// del constructor primario. Si quantity fuera parte de ese constructor
// como MutableState, copy() y equals() compararían el contenedor
// State, no el valor entero — comportamiento incorrecto.
// Como clase normal, controlamos exactamente qué se compara y cómo
// se muestra, sin efectos secundarios inesperados.
class ReceiptItem(
    val id       : Int,
    val name     : String,
    val price    : Int,
    val imageRes : Int,
    initialQuantity : Int = 1          // parámetro de inicialización, NO propiedad
) {
    // ── quantity como State observable de Compose ─────────────────
    // Delegado a mutableIntStateOf: cualquier composable que lea
    // `item.quantity` se recompone automáticamente al cambiar.
    // initialQuantity es el parámetro del constructor — se usa solo
    // para inicializar el State y no queda expuesto como propiedad.
    var quantity by mutableIntStateOf(initialQuantity)

    // ── equals y hashCode manuales basados solo en `id` ──────────
    // Esto permite que LazyColumn con key = { it.id } funcione
    // correctamente y que animateItemPlacement identifique cada
    // ítem de forma estable aunque quantity cambie.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReceiptItem) return false
        return id == other.id
    }

    override fun hashCode(): Int = id

    override fun toString(): String =
        "ReceiptItem(id=$id, name=$name, price=$price, quantity=$quantity)"
}