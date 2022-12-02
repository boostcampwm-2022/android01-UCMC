package com.gta.domain.usecase.cardetail.edit

import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Coordinate
import com.gta.domain.model.RentState
import com.gta.domain.model.UpdateCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCarDetailDataUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(
        carId: String,
        image: List<String>?,
        price: Int,
        comment: String,
        rentState: RentState,
        availableDate: AvailableDate,
        location: String,
        coordinate: Coordinate
    ): Flow<Boolean> {
        return carRepository.updateCarDetail(
            carId,
            UpdateCar(
                image ?: emptyList(),
                price,
                comment,
                rentState,
                availableDate,
                location,
                coordinate
            )
        )
    }
}
