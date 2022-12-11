package com.gta.data.repository

import com.gta.data.model.toProfile
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.SimpleReservation
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val reservationDataSource: ReservationDataSource
) : UserRepository {

    override fun getUserProfile(uid: String): Flow<UserProfile> = callbackFlow {
        userDataSource.getUser(uid).first()?.let { profile ->
            trySend(profile.toProfile(uid))
        } ?: trySend(UserProfile())
        awaitClose()
    }

    override fun getNowReservation(uid: String, carId: String): Flow<UCMCResult<SimpleReservation>> =
        callbackFlow {
            val reservation =
                reservationDataSource.getRentingStateReservations(uid).first().find { reservation ->
                    reservation.carId == carId
                }
            trySend(UCMCResult.Success(reservation ?: SimpleReservation()))
            awaitClose()
        }
}
