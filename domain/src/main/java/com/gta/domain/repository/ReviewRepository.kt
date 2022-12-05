package com.gta.domain.repository

import com.gta.domain.model.ReviewDTO
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserReview

interface ReviewRepository {
    suspend fun addReview(opponentId: String, reservationId: String, review: UserReview): UCMCResult<Unit>
    suspend fun getReviewDTO(uid: String, reservationId: String): UCMCResult<ReviewDTO>
}
