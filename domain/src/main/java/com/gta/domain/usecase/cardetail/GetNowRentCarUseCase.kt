package com.gta.domain.usecase.cardetail

import com.gta.domain.model.Reservation
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// 확인 필요
class GetNowRentCarUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(uid: String, carId: String): Flow<Reservation?> {
        return userRepository.getNowReservation(uid, carId)
    }
}
