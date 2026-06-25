// data/api/request/RegisterRequest.kt
package com.example.appifood_movil.data.api.request

data class RegisterRequest(
    val name                  : String,
    val email                 : String,
    val password              : String,
    val password_confirmation : String
)