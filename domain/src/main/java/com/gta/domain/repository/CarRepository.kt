package com.gta.domain.repository

import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.SimpleCar
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getOwnerId(carId: String): String
    fun getNowRentUserId(carId: String): Flow<String?>
    fun getCarData(carId: String): Flow<CarDetail>
    fun getCarRentInfo(carId: String): Flow<CarRentInfo>
    fun getSimpleCarList(ownerId: String): Flow<List<SimpleCar>>
    fun getAllCars(): Flow<List<SimpleCar>>
    fun removeCar(userId: String, carId: String): Flow<Boolean>
}
