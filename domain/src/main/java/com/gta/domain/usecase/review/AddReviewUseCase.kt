package com.gta.domain.usecase.review

import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserReview
import com.gta.domain.repository.ReviewRepository
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(
        opponentId: String,
        reservationId: String,
        review: UserReview
    ): UCMCResult<Unit> = repository.addReview(opponentId, reservationId, review)
}
