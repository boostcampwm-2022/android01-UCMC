package com.gta.domain.usecase.reservation

import com.gta.domain.model.CarRentInfo
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCarRentInfoUseCase @Inject constructor(private val repository: CarRepository) {
    operator fun invoke(carId: String): Flow<CarRentInfo> {
        return repository.getCarRentInfo(carId)
    }
}
