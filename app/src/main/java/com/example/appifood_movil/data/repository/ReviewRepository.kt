// data/repository/ReviewRepository.kt
package com.example.appifood_movil.data.repository

import com.example.appifood_movil.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val reviewsCollection = firestore.collection("reviews")

    // Guardar una reseña
    suspend fun saveReview(review: Review): Boolean {
        return try {
            val docRef = if (review.id.isEmpty()) reviewsCollection.document() else reviewsCollection.document(review.id)
            val reviewWithId = review.copy(id = docRef.id)
            docRef.set(reviewWithId).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Error al guardar reseña")
            false
        }
    }

    // Obtener reseñas de un restaurante en tiempo real
    fun getReviewsForRestaurant(restaurantUid: String): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .whereEqualTo("restaurantUid", restaurantUid)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Review::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    // Obtener calificación promedio de un restaurante
    suspend fun getAverageRating(restaurantUid: String): Double {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("restaurantUid", restaurantUid)
                .get()
                .await()
            val reviews = snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
            if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
        } catch (e: Exception) {
            Timber.e(e, "Error al calcular promedio")
            0.0
        }
    }
}