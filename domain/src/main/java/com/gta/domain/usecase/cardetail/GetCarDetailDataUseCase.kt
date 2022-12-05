package com.gta.domain.usecase.cardetail

import com.gta.domain.model.CarDetail
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCarDetailDataUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String): Flow<UCMCResult<CarDetail>> {
        return carRepository.getCarData(carId)
    }
}
