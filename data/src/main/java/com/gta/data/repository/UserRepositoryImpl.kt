package com.gta.data.repository

import com.gta.data.model.toProfile
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.SimpleReservation
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
        val profile = userDataSource.getUser(uid).first()?.toProfile(uid) ?: UserProfile()
        trySend(profile)
        awaitClose()
    }

    override fun getNowReservation(uid: String, carId: String): Flow<SimpleReservation> = callbackFlow {
        val reservation = reservationDataSource.getRentingStateReservations(uid).first().find { reservation ->
            reservation.carId == carId
        }
        trySend(reservation ?: SimpleReservation())
        awaitClose()
    }
}
