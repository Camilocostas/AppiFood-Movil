// data/repository/ReviewRepository.kt
package com.example.appifood_movil.data.repository

import com.example.appifood_movil.data.model.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor() {

    suspend fun saveReview(review: Review): Boolean = true

    fun getReviewsForRestaurant(restaurantUid: String): Flow<List<Review>> = flow {
        emit(emptyList())
    }

    suspend fun getAverageRating(restaurantUid: String): Double = 4.5
}
