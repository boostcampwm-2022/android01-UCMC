package com.gta.domain.usecase.review

import com.gta.domain.model.UserReview
import com.gta.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    operator fun invoke(
        opponentId: String,
        reservationId: String,
        review: UserReview
    ): Flow<Boolean> = repository.addReview(opponentId, reservationId, review)
}
