// data/repository/OrderRepository.kt
package com.example.appifood_movil.data.repository

import com.example.appifood_movil.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject  // ✅ Agregar este import
import javax.inject.Singleton

@Singleton  // ✅ Agregar Singleton
class OrderRepository @Inject constructor(  // ✅ Agregar @Inject
    private val firestore: FirebaseFirestore
) {
    private val ordersCollection = firestore.collection("orders")

    /**
     * Obtener todos los pedidos en tiempo real
     */
    fun getOrdersRealtime(): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e("Error listening to orders: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                } ?: emptyList()

                trySend(orders)
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Obtener pedidos por estado
     */
    fun getOrdersByStatus(status: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .whereEqualTo("status", status)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                } ?: emptyList()

                trySend(orders)
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Obtener un pedido por ID
     */
    suspend fun getOrderById(orderId: String): Order? {
        return try {
            val doc = ordersCollection.document(orderId).get().await()
            doc.toObject(Order::class.java)?.copy(orderId = doc.id)
        } catch (e: Exception) {
            Timber.e("Error getting order: ${e.message}")
            null
        }
    }

    /**
     * Actualizar estado del pedido
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            ordersCollection.document(orderId)
                .update("status", status)
                .await()
            true
        } catch (e: Exception) {
            Timber.e("Error updating order status: ${e.message}")
            false
        }
    }

    /**
     * Obtener pedidos pendientes
     */
    suspend fun getPendingOrders(): List<Order> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("status", "pending")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(orderId = doc.id)
            }
        } catch (e: Exception) {
            Timber.e("Error getting pending orders: ${e.message}")
            emptyList()
        }
    }
}