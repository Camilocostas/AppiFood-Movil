package com.example.appifood_movil.data.api

import com.example.appifood_movil.data.api.response.ApiResponse
import com.example.appifood_movil.data.api.response.RestaurantDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("restaurants")
    suspend fun getRestaurants(): ApiResponse<List<RestaurantDto>>

    @GET("restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: Int): ApiResponse<RestaurantDto>
}
