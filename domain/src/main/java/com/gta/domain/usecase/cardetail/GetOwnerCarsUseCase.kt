package com.gta.domain.usecase.cardetail

import com.gta.domain.model.SimpleCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetOwnerCarsUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(ownerId: String): Flow<List<SimpleCar>> {
        return MutableStateFlow(
            listOf(
                SimpleCar("1", "[말티즈] 멍멍", ""),
                SimpleCar("1", "[모닝 구형] 여기저기 가기 좋습니다", "")
            )
        )
    }
}
