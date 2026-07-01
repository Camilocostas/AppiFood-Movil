package com.example.appifood_movil.data.api.response

data class AddressesResponse(
    val data: List<AddressDto>
)

data class AddressDto(
    val id: Int,
    val title: String,
    val address: String,
    val details: String?,
    val latitude: Double?,
    val longitude: Double?
)
