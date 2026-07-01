package com.example.appifood_movil.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class ReceiptItem(
    val id: Int,
    val name: String,
    val price: Int,
    val imageRes: Int = 0,
    val imageUrl: String? = null,
    val adiciones: List<String> = emptyList(),
    initialQuantity: Int = 1
) {
    var quantity by mutableIntStateOf(initialQuantity)
        private set

    fun increaseQuantity() { quantity++ }
    fun decreaseQuantity() { if (quantity > 1) quantity-- }
    fun addQuantity(amount: Int) { quantity += amount }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReceiptItem) return false
        return id == other.id
    }
    override fun hashCode(): Int = id
}