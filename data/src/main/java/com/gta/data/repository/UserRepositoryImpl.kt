package com.gta.data.repository

import com.gta.data.model.toProfile
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.SimpleReservation
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    // 현재 유저가 이 차에 대한 대여중인 예약 정보 (실시간)
    override fun getNowReservation(
        uid: String,
        carId: String
    ): Flow<UCMCResult<SimpleReservation>> {
        return reservationDataSource.getRentingStateReservations(uid).map {
            UCMCResult.Success(
                it.find { reservation -> reservation.carId == carId }
                    ?: SimpleReservation()
            )
        }
    }
}
