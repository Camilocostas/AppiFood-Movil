package com.example.appifood_movil.data.model

data class UserData(
    val names: String = "",
    val lastNames: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", System.currentTimeMillis())
}