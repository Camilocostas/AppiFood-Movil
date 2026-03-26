package com.example.appifood_movil.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Usamos 10.0.2.2 para conectar el emulador con el localhost de tu PC
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
