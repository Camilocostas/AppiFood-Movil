package com.example.appifood_movil.data.model

data class PaymentMethod(
    val id: String = "",
    val type: String, // "Nequi", "Bancolombia", "PSE", "Daviplata"
    val identifier: String, // Número de cuenta, correo, etc.
    val holderName: String, // Nombre del titular
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", false)

    fun getIcon(): String {
        return when (type) {
            "Nequi" -> "🏦"
            "Bancolombia" -> "🏛️"
            "PSE" -> "💳"
            "Daviplata" -> "📱"
            else -> "💰"
        }
    }
}