// data/repository/OrderRepository.kt
package com.example.appifood_movil.data.repository

import com.example.appifood_movil.data.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor() {

    fun getOrdersRealtime(): Flow<List<Order>> = flow {
        emit(emptyList())
    }

    fun getOrdersByStatus(status: String): Flow<List<Order>> = flow {
        emit(emptyList())
    }

    suspend fun getOrderById(orderId: String): Order? = null

    suspend fun updateOrderStatus(orderId: String, status: String): Boolean = true

    suspend fun getPendingOrders(): List<Order> = emptyList()
}
