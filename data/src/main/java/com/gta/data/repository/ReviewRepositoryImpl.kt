package com.gta.data.repository

import com.gta.data.source.ReviewDataSource
import com.gta.domain.model.UserReview
import com.gta.domain.repository.ReviewRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val dataSource: ReviewDataSource
) : ReviewRepository {
    override fun addReview(
        opponentId: String,
        reservationId: String,
        review: UserReview
    ): Flow<Boolean> = callbackFlow {
        dataSource.addReview(opponentId, reservationId, review).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}
