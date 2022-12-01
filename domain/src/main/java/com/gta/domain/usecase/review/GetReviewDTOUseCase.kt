package com.gta.domain.usecase.review

import com.gta.domain.model.ReviewDTO
import com.gta.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReviewDTOUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    operator fun invoke(uid: String, reservationId: String): Flow<ReviewDTO> =
        repository.getReviewDTO(uid, reservationId)
}
