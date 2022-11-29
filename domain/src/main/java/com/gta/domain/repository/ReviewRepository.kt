package com.gta.domain.repository

import com.gta.domain.model.ReviewDTO
import com.gta.domain.model.UserReview
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun addReview(opponentId: String, reservationId: String, review: UserReview): Flow<Boolean>
    fun getReviewDTO(uid: String, reservationId: String, carId: String): Flow<ReviewDTO>
}
