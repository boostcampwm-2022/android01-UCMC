package com.gta.domain.usecase.cardetail.edit

import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteImagesUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(images: List<String>): Flow<Boolean> {
        return carRepository.deleteImagesStorage(images)
    }
}
