package com.gta.data.repository

import com.gta.data.model.Car
import com.gta.data.model.toCarRentInfo
import com.gta.data.model.toDetailCar
import com.gta.data.model.toProfile
import com.gta.data.model.toSimple
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val carDataSource: CarDataSource,
    private val reservationDataSource: ReservationDataSource
) : CarRepository {
    override fun getOwnerId(carId: String): Flow<String> {
        return getCar(carId).map { it.ownerId }
    }

    override fun getCarRentState(carId: String): Flow<RentState> {
        return getCar(carId).map { it.toDetailCar("", UserProfile()).rentState }
    }

    override fun getCarData(carId: String): Flow<CarDetail> = callbackFlow {
        carDataSource.getCar(carId).first()?.let { car ->
            userDataSource.getUser(car.ownerId).first()?.let { ownerInfo ->
                trySend(
                    car.toDetailCar(
                        car.pinkSlip.informationNumber,
                        ownerInfo.toProfile(car.ownerId)
                    )
                )
            } ?: trySend(CarDetail())
        } ?: trySend(CarDetail())
        awaitClose()
    }

    override fun getCarRentInfo(carId: String): Flow<CarRentInfo> = callbackFlow {
        carDataSource.getCar(carId).first()?.let { car ->
            trySend(car.toCarRentInfo(reservationDataSource.getCarReservationDates(car).first()))
        } ?: trySend(CarRentInfo())
        awaitClose()
    }

    private fun getCar(carId: String): Flow<Car> = callbackFlow {
        val car = carDataSource.getCar(carId).first() ?: Car()
        trySend(car)
        awaitClose()
    }

    override fun getSimpleCarList(ownerId: String): Flow<List<SimpleCar>> = callbackFlow {
        userDataSource.getUser(ownerId).first()?.let { userInfo ->
            if (userInfo.myCars.isNotEmpty()) {
                carDataSource.getOwnerCars(userInfo.myCars).first().map { car ->
                    car.toSimple(car.pinkSlip.informationNumber)
                }.also { result ->
                    trySend(result)
                }
            } else {
                trySend(emptyList())
            }
        } ?: trySend(emptyList())
        awaitClose()
    }

    override fun getAllCars(): Flow<List<SimpleCar>> = callbackFlow {
        val cars = carDataSource.getAllCars().first()
        trySend(cars.map { it.toSimple(it.pinkSlip.informationNumber) })
        awaitClose()
    }

    override fun removeCar(userId: String, carId: String): Flow<Boolean> = callbackFlow {
        if (carDataSource.removeCar(carId).first()) {
            userDataSource.getUser(userId).first()?.let { userInfo ->
                val newCars = userInfo.myCars.filter { it != carId }
                trySend(userDataSource.removeCar(userId, newCars).first())
            } ?: trySend(false)
        } else {
            trySend(false)
        }
        awaitClose()
    }
}
