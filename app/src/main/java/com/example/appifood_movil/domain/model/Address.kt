package com.example.appifood_movil.domain.model

data class Address(
    val id: Int = 0,
    val title: String = "",
    val address: String = "",
    val details: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
