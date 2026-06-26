// data/api/ApiService.kt
package com.example.appifood_movil.data.api

import com.example.appifood_movil.data.api.request.LoginRequest
import com.example.appifood_movil.data.api.request.RegisterRequest
import com.example.appifood_movil.data.api.request.OrderRequest
import com.example.appifood_movil.data.api.request.CartRequest
import com.example.appifood_movil.data.api.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── AUTH ──────────────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<MessageResponse>

    @GET("me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    // ── RESTAURANTES ──────────────────────────────────────────────
    @GET("restaurants")
    suspend fun getRestaurants(): Response<RestaurantsResponse>

    @GET("restaurants/{id}")
    suspend fun getRestaurantById(
        @Path("id") id: Int
    ): Response<RestaurantDetailResponse>

    @GET("restaurants/{id}/reviews")
    suspend fun getRestaurantReviews(
        @Path("id") id: Int
    ): Response<ReviewsResponse>

    // ── PEDIDOS ───────────────────────────────────────────────────
    @GET("user/orders")
    suspend fun getUserOrders(
        @Header("Authorization") token: String
    ): Response<OrdersResponse>

    @POST("user/orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Response<OrderCreatedResponse>

    // ── CARRITO ───────────────────────────────────────────────────
    @GET("user/cart")
    suspend fun getCart(
        @Header("Authorization") token: String
    ): Response<CartResponse>

    @POST("user/cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: CartRequest
    ): Response<CartResponse>

    // ── FAVORITOS ─────────────────────────────────────────────────
    @GET("user/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): Response<FavoritesResponse>

    // ── NOTIFICACIONES ────────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<NotificationsResponse>

    // ── HEALTH CHECK ──────────────────────────────────────────────
    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>
}