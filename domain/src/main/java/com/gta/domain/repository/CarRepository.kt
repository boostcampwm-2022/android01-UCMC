package com.gta.domain.repository

import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.Coordinate
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UpdateCar
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getOwnerId(carId: String): Flow<String>
    fun getCarRentState(carId: String): Flow<RentState>
    fun getCarData(carId: String): Flow<UCMCResult<CarDetail>>
    fun updateCarDetail(carId: String, update: UpdateCar): Flow<Boolean>
    fun getCarRentInfo(carId: String): Flow<CarRentInfo>
    fun getSimpleCar(carId: String): Flow<SimpleCar>
    fun getSimpleCarList(ownerId: String): Flow<UCMCResult<List<SimpleCar>>>
    fun getAllCars(): Flow<List<SimpleCar>>
    fun getNearCars(min: Coordinate, max: Coordinate): Flow<UCMCResult<List<SimpleCar>>>
    suspend fun removeCar(userId: String, carId: String): UCMCResult<Unit>
    fun setCarImagesStorage(carId: String, images: List<String>): Flow<List<UCMCResult<String>>>
    fun deleteImagesStorage(images: List<String>): Flow<Boolean>
}
