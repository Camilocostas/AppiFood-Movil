package com.example.appifood_movil.data.api.request

data class AddressRequest(
    val title: String,
    val address: String,
    val details: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
