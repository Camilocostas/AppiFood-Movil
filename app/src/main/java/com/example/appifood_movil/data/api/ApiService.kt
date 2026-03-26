package com.example.appifood_movil.data.api

import com.example.appifood_movil.data.model.Restaurant
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("restaurants")
    suspend fun getRestaurants(): List<Restaurant>

    @GET("restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: String): Restaurant
    
    // Aquí puedes añadir los de Auth (Login, Register) conforme los necesites
}
