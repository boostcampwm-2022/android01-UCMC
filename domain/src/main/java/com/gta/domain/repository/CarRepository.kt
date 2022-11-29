package com.gta.domain.repository

import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.Coordinate
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UpdateCar
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getOwnerId(carId: String): Flow<String>
    fun getCarRentState(carId: String): Flow<RentState>
    fun getCarData(carId: String): Flow<CarDetail>
    fun updateCarDetail(carId: String, update: UpdateCar): Flow<Boolean>
    fun getCarRentInfo(carId: String): Flow<CarRentInfo>
    fun getSimpleCar(carId: String): Flow<SimpleCar>
    fun getSimpleCarList(ownerId: String): Flow<List<SimpleCar>>
    fun getAllCars(): Flow<List<SimpleCar>>
    fun getNearCars(min: Coordinate, max: Coordinate): Flow<List<SimpleCar>>
    fun removeCar(userId: String, carId: String): Flow<Boolean>
}
