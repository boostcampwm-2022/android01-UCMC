package com.gta.domain.repository

import com.gta.domain.model.CarDetail
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getOwnerId(carId: String): String
    fun getNowRentUser(carId: String): String?
    fun getCarData(carId: String): Flow<CarDetail>
}
