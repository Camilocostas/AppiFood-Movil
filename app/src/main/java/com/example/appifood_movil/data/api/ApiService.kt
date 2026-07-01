package com.example.appifood_movil.data.api

import com.example.appifood_movil.data.api.request.*
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

    // ── RESTAURANTES (dueño) ──────────────────────────────────────
    @GET("restaurants/owner/{uid}")
    suspend fun getRestaurantByOwner(
        @Path("uid") uid: String,
        @Header("Authorization") token: String
    ): Response<RestaurantDetailResponse>

    @POST("restaurants")
    suspend fun createRestaurant(
        @Header("Authorization") token: String,
        @Body restaurant: RestaurantRequest
    ): Response<RestaurantCreatedResponse>

    @PUT("restaurants/{id}")
    suspend fun updateRestaurant(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body restaurant: RestaurantRequest
    ): Response<MessageResponse>

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

    // ── DIRECCIONES (CRUD) ────────────────────────────────────────
    @GET("user/addresses")
    suspend fun getAddresses(
        @Header("Authorization") token: String
    ): Response<AddressesResponse>

    @POST("user/addresses")
    suspend fun createAddress(
        @Header("Authorization") token: String,
        @Body request: AddressRequest
    ): Response<MessageResponse>

    @DELETE("user/addresses/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MessageResponse>

    // ── NOTIFICACIONES ────────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<NotificationsResponse>

    // ── HEALTH CHECK ──────────────────────────────────────────────
    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>
}
