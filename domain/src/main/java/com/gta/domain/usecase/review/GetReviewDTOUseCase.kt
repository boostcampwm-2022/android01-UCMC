package com.gta.domain.usecase.review

import com.gta.domain.model.ReviewDTO
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.ReviewRepository
import javax.inject.Inject

class GetReviewDTOUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(uid: String, reservationId: String): UCMCResult<ReviewDTO> =
        repository.getReviewDTO(uid, reservationId)
}
