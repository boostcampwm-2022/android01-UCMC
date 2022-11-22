package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.CarRepository
import javax.inject.Inject

class SetStateAtCarDetailUseCase @Inject constructor(
    private val carRepository: CarRepository
)  {

}