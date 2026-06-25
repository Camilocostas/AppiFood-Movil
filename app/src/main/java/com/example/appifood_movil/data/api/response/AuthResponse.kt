// data/api/response/AuthResponse.kt
package com.example.appifood_movil.data.api.response

data class AuthResponse(
    val token : String,
    val user  : UserDto
)

data class UserDto(
    val id    : Int,
    val name  : String,
    val email : String,
    val role  : String
)