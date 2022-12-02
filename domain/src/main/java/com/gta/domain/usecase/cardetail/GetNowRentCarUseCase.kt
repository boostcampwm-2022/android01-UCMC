package com.gta.domain.usecase.cardetail

import com.gta.domain.model.SimpleReservation
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNowRentCarUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(uid: String, carId: String): Flow<SimpleReservation> {
        return userRepository.getNowReservation(uid, carId)
    }
}
