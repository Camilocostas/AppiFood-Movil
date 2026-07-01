// data/api/response/AuthResponse.kt
package com.example.appifood_movil.data.api.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: AuthData? = null
)

data class AuthData(
    @SerializedName("token")
    val token: String = "",

    @SerializedName("user")
    val user: UserDto? = null
)

data class UserDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("rol")
    val rol: String = "usuario"
)